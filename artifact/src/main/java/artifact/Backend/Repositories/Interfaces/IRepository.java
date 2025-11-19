package artifact.Backend.Repositories.Interfaces;
import javafx.collections.ObservableList;

public interface IRepository<T> {
    ObservableList<T> getAll();
    T findById(long id);
    void add(T item);
    void update(T item);
    void delete(long id);

    void refreshFromCloud();
}
