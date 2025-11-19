package artifact.Backend.Supabase;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import artifact.Backend.Services.Impl.SyncService;

public class SyncScheduler {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void start() {
        // A hook to shutdown the scheduler when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(SyncScheduler::stop));

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Ensure SyncService exists or handle the null case if not yet implemented
                if (SyncService.class != null) {
                    SyncService.syncAll();
                }
            } catch (Exception e) {
                System.err.println("Error during sync: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void stop() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }
}