package artifact.Backend;
import javafx.scene.control.Alert;

/**
 * Utility class for showing common alerts.
 * Follows SRP. Unchanged from previous refactor.
 */
public class AlertUtils {

    public static void infoBox(String infoMessage, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void errorBox(String infoMessage, String title) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}