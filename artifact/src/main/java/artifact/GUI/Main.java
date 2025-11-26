package artifact.GUI;

import artifact.Backend.View;
import artifact.Backend.Controller.NavigationService;
import artifact.Backend.Supabase.SyncScheduler; // <--- Import this instead
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        try {
            System.out.println("App started. Icon loading skipped for programmatic build.");
        } catch (Exception e) {
            System.err.println("Icon image not found.");
        }

        NavigationService navigation = NavigationService.getInstance();
        navigation.setPrimaryStage(primaryStage);

        // --- REPLACEMENT CODE ---
        // Instead of manually fetching once, we start the background sync service.
        // This will run every 5 seconds (as defined in SyncScheduler).
        try {
            SyncScheduler.start(); 
            System.out.println("Background Sync Service Started.");
        } catch (Exception e) {
            System.err.println("Failed to start Sync Scheduler: " + e.getMessage());
        }
        // ------------------------

        navigation.navigateTo(View.LOGIN);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Good practice: Ensure threads stop when you close the window
        SyncScheduler.stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}