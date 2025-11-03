package artifact.Backend.Supabase;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;
/**
 * Handles communication with the Supabase Backend via REST API.
 * <p>
 * This class uses a custom Gson configuration to correctly handle Java 8 Date/Time objects
 * (LocalDate, LocalDateTime) by serializing them as ISO strings, avoiding reflection errors.
 * </p>
 */
public class SupabaseSync {


    private static final Dotenv dotenv = Dotenv.load();
    private static final String SUPABASE_URL = dotenv.get("SUPABASE_URL");
    private static final String API_KEY = dotenv.get("SUPABASE_API_KEY");    private static final String BASE_PATH = "/rest/v1/"; 
    
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) 
            .create();

    // 1. READ (Select)
    // Overload to support Generic Types passed from Repositories
    public static <T> List<T> selectAll(String tableName, Type listType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + BASE_PATH + tableName + "?select=*"))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Use the Type directly for deserialization
                return gson.fromJson(response.body(), listType);
            }
        } catch (Exception e) {
            System.err.println("Supabase select exception: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // 2. CREATE (Upsert - Insert or Update if ID exists)
    public static <T> void upsert(String tableName, T rowObject) {
        try {
            String json = gson.toJson(rowObject);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + BASE_PATH + tableName))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .method("POST", HttpRequest.BodyPublishers.ofString(json))
                    .header("Prefer", "resolution=merge-duplicates")
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e) {
            System.out.println("Supabase upsert failed: " + e.getMessage());
        }
    }

    // 3. UPDATE (Patch - Modify specific record by ID)
    public static <T> void update(String tableName, long id, T rowObject) {
        try {
            String json = gson.toJson(rowObject);
            // Target specific ID using query param ?id=eq.{id}
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + BASE_PATH + tableName + "?id=eq." + id))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 400) {
                 System.err.println("Supabase Update Failed: " + response.statusCode() + " " + response.body());
            }
        }
        catch (Exception e) {
            System.out.println("Supabase update failed: " + e.getMessage());
        }
    }

    // 4. DELETE
    public static void delete(String tableName, long id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SUPABASE_URL + BASE_PATH + tableName + "?id=eq." + id))
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .DELETE()
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (Exception e) {
            System.out.println("Supabase delete failed: " + e.getMessage());
        }
    }

    // --- INTERNAL TYPE ADAPTERS ---

    /**
     * Adapter to convert LocalDate to simple Strings (YYYY-MM-DD) for JSON.
     */
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Robust parsing: Handle empty strings safely if necessary
            if (json.getAsString().isEmpty()) return null;
            return LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    /**
     * Adapter to convert LocalDateTime to simple Strings (ISO-8601) for JSON.
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.getAsString().isEmpty()) return null;
            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}