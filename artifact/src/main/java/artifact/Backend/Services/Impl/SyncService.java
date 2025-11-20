package artifact.Backend.Services.Impl;
import artifact.Backend.Supabase.SupabaseSync;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing offline-first data synchronization.
 * <p>
 * This service queues database operations (upserts) in memory and 
 * flushes them to the remote Supabase instance when triggered.
 * Useful for batching updates or handling intermittent connectivity.
 * </p>
 */
public class SyncService {
    
    // In-memory queue for pending operations
    private static final List<SyncItem> pending = new ArrayList<>();

    // Internal record to hold operation metadata
    private record SyncItem(String table, Object data) {}

    /**
     * Adds an operation to the synchronization queue.
     * This method is synchronized to ensure thread safety.
     *
     * @param tableName The target table in the database.
     * @param data      The object/data to upsert.
     */
    public static synchronized void enqueue(String tableName, Object data) {
        pending.add(new SyncItem(tableName, data));
    }

    /**
     * Flushes the queue, attempting to push all pending items to Supabase.
     * Clears the pending list upon completion.
     */
    public static synchronized void syncAll() {
        for (SyncItem item : pending) {
            SupabaseSync.upsert(item.table, item.data);
        }
        pending.clear();
    }
}