package artifact.Backend.Repositories.Impl;

import artifact.Backend.Config.GsonProvider;
import artifact.Backend.Services.Impl.SyncService;
import artifact.Backend.Supabase.SupabaseSync;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

// T = Model Class (e.g., User)
public abstract class BaseJsonRepository<T> {
    protected final String filePath;
    protected final ObservableList<T> dataList;
    protected final Type listType;
    protected final String supabaseTable;
    
    // Function to extract ID from an entity (e.g., User::id)
    private final Function<T, Long> idExtractor;

    public BaseJsonRepository(String filename, Type listType, String supabaseTable, Function<T, Long> idExtractor) {
        this.filePath = "src/main/resources/data/" + filename;
        this.listType = listType;
        this.supabaseTable = supabaseTable;
        this.idExtractor = idExtractor;
        this.dataList = loadData();
        
        // Hook for seeding data if empty
        if (this.dataList.isEmpty()) {
            seedData();
        }
    }

    private ObservableList<T> loadData() {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return FXCollections.observableArrayList();

        try (FileReader reader = new FileReader(path.toFile())) {
            List<T> list = GsonProvider.getGson().fromJson(reader, listType);
            return list != null ? FXCollections.observableArrayList(list) : FXCollections.observableArrayList();
        } catch (IOException e) {
            System.err.println("Error loading " + filePath + ": " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }

    protected void save() {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            try (FileWriter writer = new FileWriter(path.toFile())) {
                GsonProvider.getGson().toJson(dataList, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Common CRUD
    public ObservableList<T> getAll() { return dataList; }

    public T findById(long id) {
        return dataList.stream()
                .filter(item -> idExtractor.apply(item) == id)
                .findFirst().orElse(null);
    }

    public void add(T item) {
        dataList.add(item);
        save();
        if (supabaseTable != null) {
            SyncService.enqueue(supabaseTable, item);
        }
    }

    public void update(T item) {
        long id = idExtractor.apply(item);
        T existing = findById(id);
        if (existing != null) {
            int index = dataList.indexOf(existing);
            dataList.set(index, item);
            save();
            
            // Note: SyncService.update() is not available in context, 
            // so we only handle local updates here.
        }
    }
    
    // Feature Completed: Generic Delete
    public void delete(long id) {
        boolean removed = dataList.removeIf(item -> idExtractor.apply(item) == id);
        if (removed) {
            save();
            // Note: SyncService.delete() is not available in context,
            // so we only handle local deletions here.
        }
    }

    protected long generateNextId() {
        return dataList.stream()
                .mapToLong(idExtractor::apply)
                .max().orElse(0) + 1;
    }

    // Override this in subclasses to provide default data
    protected void seedData() {} 

    // Inside BaseJsonRepository.java

    public void refreshFromCloud() {
        System.out.println("Syncing " + supabaseTable + " from Cloud...");
        
        // 1. Fetch latest data from Supabase
        List<T> cloudData = SupabaseSync.selectAll(supabaseTable, listType);
        
        // 2. Update Memory (ObservableList)
        // We use setAll to notify the UI automatically
        dataList.setAll(cloudData);
        
        // 3. Update Local JSON File
        save();
    }
}