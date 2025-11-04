package artifact.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private final Stage stage;
    private static final double WIDTH = 750;
    private static final double HEIGHT = 560;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    /**
     * Loads FXML, sets scene to stage, and returns the controller
     */
    public <T> T loadScene(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/App/" + fxmlFile));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/logo.png")
        )));
        return loader.getController();
    }
}
