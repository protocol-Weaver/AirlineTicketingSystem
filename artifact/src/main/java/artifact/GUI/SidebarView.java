package artifact.GUI;
import artifact.Backend.UserSession;
import artifact.Backend.View;
import artifact.Backend.Controller.BaseController;
import javafx.scene.Cursor;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A reusable sidebar component (a "widget").
 * This single class builds the entire left-hand navigation menu
 * and user info panel, following the DRY principle.
 */
public class SidebarView extends AnchorPane {

    private final BaseController controller;
    private final UserSession userSession;

    public SidebarView(BaseController controller, View activeView) {
        this.controller = controller;
        this.userSession = UserSession.getInstance();

        // --- Sidebar Styling ---
        setPrefHeight(560.0);
        setPrefWidth(200.0);
        setStyle("-fx-background-color: #fff;");

        // --- Logo ---
        // TODO: Add logo image
        Label logoPlaceholder = new Label("LOGO");
        logoPlaceholder.setFont(Font.font("System", FontWeight.BOLD, 24));
        AnchorPane.setTopAnchor(logoPlaceholder, 30.0);
        AnchorPane.setLeftAnchor(logoPlaceholder, 60.0);
        ImageView logo = new ImageView(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        logo.setFitHeight(80.0);
        logo.setFitWidth(88.0);
        AnchorPane.setTopAnchor(logo, 16.0);
        AnchorPane.setLeftAnchor(logo, 56.0);
        getChildren().add(logo);
        getChildren().add(logoPlaceholder);

        // --- Navigation Links ---
        getChildren().addAll(
            createNavButton("Home", "/images/i1.png", 120.0, controller::goHome, activeView == View.HOME),
            createNavButton("Airports", "/images/i2.png", 165.0, controller::goAirports, activeView == View.AIRPORTS),
            createNavButton("Aircrafts", "/images/i3.png", 210.0, controller::goAirCrafts, activeView == View.AIRCRAFTS),
            createNavButton("Flights", "/images/i4.png", 255.0, controller::goFlights, activeView == View.FLIGHTS),
            createNavButton("Crews", "/images/i5.png", 300.0, controller::goCrews, activeView == View.CREWS),
            createNavButton("Reservations", "/images/i6.png", 345.0, controller::goReservations, activeView == View.RESERVATIONS),
            createNavButton("Tickets", "/images/i7.png", 390.0, controller::goTickets, activeView == View.TICKETS),
            createNavButton("Log out", "/images/i8.png", 435.0, controller::goLogin, false)
        );

        // --- Admin Info Panel ---
        Label adminNameLabel = new Label(userSession.getAdminName());
        adminNameLabel.setTextFill(Color.web("#080c53"));
        adminNameLabel.setFont(Font.font("System", FontWeight.BOLD, 16.0));
        AnchorPane.setBottomAnchor(adminNameLabel, 42.0);
        AnchorPane.setLeftAnchor(adminNameLabel, 14.0);

        Label adminEmailLabel = new Label(userSession.getAdminEmail());
        adminEmailLabel.setTextFill(Color.web("#00a4bf"));
        adminEmailLabel.setFont(Font.font("System", 12.0));
        AnchorPane.setBottomAnchor(adminEmailLabel, 15.0);
        AnchorPane.setLeftAnchor(adminEmailLabel, 14.0);

        getChildren().addAll(adminNameLabel, adminEmailLabel);
    }

    /**
     * Helper factory method to create a single navigation button.
     * @param text The text for the hyperlink.
     * @param iconPath Path to the icon (currently unused).
     * @param layoutY The Y-position of the button.
     * @param action The method to call on click (e.g., controller::goHome).
     * @param isActive Whether this is the currently active page.
     * @return An AnchorPane representing the styled button.
     */
    private AnchorPane createNavButton(String text, String iconPath, double layoutY, Runnable action, boolean isActive) {
        AnchorPane navPane = new AnchorPane();
        navPane.setPrefHeight(40.0);
        navPane.setPrefWidth(180.0);
        navPane.setLayoutX(10.0);
        navPane.setLayoutY(layoutY);
        
        String style = isActive
            ? "-fx-background-color: #00a4bf; -fx-background-radius: 8;"
            : "-fx-background-color: #f4f4f4; -fx-background-radius: 8;";
        navPane.setStyle(style);
        navPane.setCursor(Cursor.HAND);
        navPane.setOnMouseClicked(e -> action.run());

        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitHeight(22.0);
        icon.setFitWidth(22.0);
        icon.setLayoutX(30.0);
        icon.setLayoutY(8.0);
        
        Hyperlink link = new Hyperlink(text);
        link.setTextFill(isActive ? Color.WHITE : Color.web("#080c53"));
        link.setFont(Font.font("System", 14.0));
        link.setLayoutX(56.0); // Adjust this if icons are added
        link.setLayoutY(8.0);
        link.setOnAction(e -> action.run());

        navPane.getChildren().add(link); // Add icon here as well
        return navPane;
    }
}
