package artifact.GUI;

import artifact.Backend.View;
import artifact.Backend.Controller.BaseController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * UPDATED: Stylish navigation bar for the customer flow.
 * Now includes the "Support" link.
 */
public class UserNavbarView extends AnchorPane {

    private final BaseController controller;

    public UserNavbarView(BaseController controller, View activeView) {
        this.controller = controller;
        
        // Modern Navbar Height & Shadow
        setPrefHeight(80.0);
        setStyle("-fx-background-color: #ffffff; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);");
        setPadding(new Insets(0, 48, 0, 48)); 

        HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        // Lock container to edges
        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);

        // --- Logo Section ---
        Label logoText = new Label("SKYLINE");
        logoText.setFont(Font.font("Montserrat", FontWeight.BOLD, 24));
        logoText.setTextFill(Color.web("#080c53")); // Dark Navy
        
        Label logoSub = new Label("TRAVEL");
        logoSub.setFont(Font.font("Montserrat", FontWeight.LIGHT, 24));
        logoSub.setTextFill(Color.web("#00a4bf")); // Brand Teal
        
        HBox logoBox = new HBox(2, logoText, logoSub);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setCursor(Cursor.HAND);
        logoBox.setOnMouseClicked(e -> controller.goUserBookingHome());

        // --- Navigation Links ---
        HBox navLinks = new HBox(40.0); // Generous spacing
        navLinks.setAlignment(Pos.CENTER_LEFT);
        
        navLinks.getChildren().addAll(
            createNavLink("Book Flight", controller::goUserBookingHome, activeView == View.USER_BOOKING_HOME),
            createNavLink("My Trips", controller::goMyBookings, activeView == View.MY_BOOKINGS),           
            createNavLink("Support", controller::goUserSupport, activeView == View.USER_SUPPORT)
        );

        // --- Spacer ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- User/Profile Section ---
        HBox userSection = new HBox(20.0);
        userSection.setAlignment(Pos.CENTER_RIGHT);
        
        // User Name Label
        VBox userText = new VBox(-2);
        userText.setAlignment(Pos.CENTER_RIGHT);
        Label welcome = new Label("Hello,");
        welcome.setFont(Font.font("System", 11));
        welcome.setTextFill(Color.web("#888"));
        
        Label userName = new Label(controller.getUserSession() != null ? controller.getUserSession().getAdminName() : "Guest");
        userName.setFont(Font.font("System", FontWeight.BOLD, 14));
        userName.setTextFill(Color.web("#333"));
        userText.getChildren().addAll(welcome, userName);
        
        // Logout Button (Pill style)
        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        logoutBtn.setTextFill(Color.web("#080c53"));
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #080c53; -fx-border-radius: 20; -fx-border-width: 1.5; -fx-padding: 5 15;");
        logoutBtn.setCursor(Cursor.HAND);
        
        logoutBtn.setOnMouseEntered(e -> {
            logoutBtn.setStyle("-fx-background-color: #080c53; -fx-text-fill: white; -fx-border-color: #080c53; -fx-border-radius: 20; -fx-border-width: 1.5; -fx-padding: 5 15;");
        });
        logoutBtn.setOnMouseExited(e -> {
            logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #080c53; -fx-border-color: #080c53; -fx-border-radius: 20; -fx-border-width: 1.5; -fx-padding: 5 15;");
        });
        logoutBtn.setOnAction(e -> controller.goLogin());

        userSection.getChildren().addAll(userText, logoutBtn);

        // --- Assemble ---
        container.getChildren().addAll(logoBox, new Region() {{ setMinWidth(60); }}, navLinks, spacer, userSection);
        getChildren().add(container);
    }

    private Label createNavLink(String text, Runnable action, boolean isActive) {
        Label link = new Label(text);
        link.setFont(Font.font("System", FontWeight.MEDIUM, 15.0));
        link.setCursor(Cursor.HAND);
        link.setOnMouseClicked(e -> action.run());
        
        if (isActive) {
            link.setTextFill(Color.web("#00a4bf"));
            link.setStyle("-fx-border-color: #00a4bf; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 4 0;");
        } else {
            link.setTextFill(Color.web("#555"));
            link.setStyle("-fx-padding: 0 0 4 0; -fx-border-width: 0 0 2 0; -fx-border-color: transparent;");
            
            link.setOnMouseEntered(e -> link.setTextFill(Color.web("#00a4bf")));
            link.setOnMouseExited(e -> link.setTextFill(Color.web("#555")));
        }
        return link;
    }
}