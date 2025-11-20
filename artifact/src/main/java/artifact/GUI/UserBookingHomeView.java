package artifact.GUI;

import artifact.Backend.Controller.UserBookingHomeController;
import artifact.Backend.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class UserBookingHomeView extends StackPane {

    private final UserBookingHomeController controller;
    private final BorderPane mainLayout = new BorderPane();

    public UserBookingHomeView() {
        this.controller = new UserBookingHomeController();
        setStyle("-fx-background-color: #f4f8fb;");

        // --- Navigation Bar ---
        mainLayout.setTop(new UserNavbarView(controller, View.USER_BOOKING_HOME));
        
        // --- Center Content ---
        VBox centerContent = new VBox(48.0);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(48.0));

        // --- Hero Text ---
        VBox heroSection = new VBox(16.0);
        heroSection.setAlignment(Pos.CENTER);
        Label heroTitle = new Label("Let's explore the world together.");
        heroTitle.setFont(Font.font("System", FontWeight.BOLD, 42.0));
        heroTitle.setTextFill(Color.web("#080c53"));
        
        Text heroSubtitle = new Text("Find the best flights for your next adventure.");
        heroSubtitle.setFont(Font.font("System", 18.0));
        heroSubtitle.setFill(Color.web("#555"));
        
        heroSection.getChildren().addAll(heroTitle, heroSubtitle);

        // --- Hero Search Buttons ---
        HBox searchButtons = new HBox(24.0);
        searchButtons.setAlignment(Pos.CENTER);
        
        VBox fromButton = createHeroButton("From", "Where are you?");
        VBox toButton = createHeroButton("To", "Where to?");
        
        Label arrow = new Label("➔");
        arrow.setFont(Font.font(24));
        arrow.setTextFill(Color.web("#ccc"));

        searchButtons.getChildren().addAll(fromButton, arrow, toButton);

        centerContent.getChildren().addAll(heroSection, searchButtons);
        mainLayout.setCenter(centerContent);
        
        getChildren().add(mainLayout);
    }

    private VBox createHeroButton(String label, String placeholder) {
        VBox box = new VBox(8);
        box.setPrefSize(250, 100);
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 4); -fx-padding: 20px;");
        box.setCursor(Cursor.HAND);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label lbl = new Label(label);
        lbl.setTextFill(Color.web("#999"));
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Label val = new Label(placeholder);
        val.setTextFill(Color.web("#080c53"));
        val.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        box.getChildren().addAll(lbl, val);
        
        box.setOnMouseClicked(e -> openWizard());
        
        return box;
    }
    
    private void openWizard() {
        // IMPORTANT: The BookingWizardView (not provided) likely calls 'controller.performSearch(...)'.
        // You may need to update BookingWizardView to use 'FlightSearchRequest' DTO if it calls the controller directly.
        BookingWizardView wizard = new BookingWizardView(controller, this::closeWizard);
        getChildren().add(wizard);
    }
    
    private void closeWizard() {
        if (getChildren().size() > 1) {
            getChildren().remove(getChildren().size() - 1);
        }
    }
}