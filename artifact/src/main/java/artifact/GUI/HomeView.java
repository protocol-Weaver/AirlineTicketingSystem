package artifact.GUI;

import artifact.Backend.View;
import artifact.Backend.Controller.HomeController;
import artifact.Backend.Models.DashboardStats; // Import the DTO
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HomeView extends AnchorPane {

    private final HomeController controller;

    public HomeView() {
        this.controller = new HomeController();

        // --- Main View Setup ---
        setPrefHeight(560.0);
        setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.HOME);
        
        // --- Main Content Area ---
        AnchorPane mainContent = new AnchorPane();
        mainContent.setPrefHeight(560.0);
        mainContent.setPrefWidth(550.0);
        AnchorPane.setTopAnchor(mainContent, 0.0);
        AnchorPane.setLeftAnchor(mainContent, 200.0); 

        // --- Header Title ---
        Label title = new Label("TRAVEL AGENCY AIR WAYS");
        title.setTextFill(Color.web("#00a4bf"));
        title.setFont(Font.font("Franklin Gothic Demi Italic", 28.0));
        AnchorPane.setTopAnchor(title, 33.0);
        AnchorPane.setLeftAnchor(title, (550.0 - 400) / 2); // Approximate centering
        
        // --- Statistics Labels (View owns these now) ---
        Label airportsCount = new Label("0");
        Label airCraftsCount = new Label("0");
        Label crewsCount = new Label("0");
        Label reservationsCount = new Label("0");
        Label flightsCount = new Label("0");
        Label passengersCount = new Label("0");

        // --- Create Stat Boxes ---
        AnchorPane airportsBox = createStatBox("Airports", airportsCount, 17.0, 90.0);
        AnchorPane aircraftsBox = createStatBox("Aircraft", airCraftsCount, 283.0, 90.0);
        AnchorPane crewsBox = createStatBox("Crews", crewsCount, 17.0, 230.0);
        AnchorPane reservationsBox = createStatBox("Reservations", reservationsCount, 283.0, 230.0);
        AnchorPane flightsBox = createStatBox("Flights", flightsCount, 17.0, 370.0);
        AnchorPane passengersBox = createStatBox("Passengers", passengersCount, 283.0, 370.0);

        mainContent.getChildren().addAll(
            title, airportsBox, aircraftsBox, crewsBox,
            reservationsBox, flightsBox, passengersBox
        );

        getChildren().addAll(sidebar, mainContent);

        // --- DATA BINDING (The Logic Change) ---
        // 1. Fetch Data from Controller (Returns DTO)
        DashboardStats stats = controller.loadStats();

        // 2. Update UI (View Responsibility)
        airportsCount.setText(String.valueOf(stats.airportCount()));
        airCraftsCount.setText(String.valueOf(stats.aircraftCount()));
        crewsCount.setText(String.valueOf(stats.crewCount()));
        reservationsCount.setText(String.valueOf(stats.reservationCount()));
        flightsCount.setText(String.valueOf(stats.flightCount()));
        passengersCount.setText(String.valueOf(stats.passengerCount()));
    }

    /**
     * Helper to create styled boxes.
     */
    private AnchorPane createStatBox(String title, Label countLabel, double layoutX, double layoutY) {
        AnchorPane box = new AnchorPane();
        box.setPrefHeight(118.0);
        box.setPrefWidth(253.0);
        box.setStyle("-fx-background-color: #fff; -fx-background-radius: 4; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        AnchorPane.setTopAnchor(box, layoutY);
        AnchorPane.setLeftAnchor(box, layoutX);

        Label titleLabel = new Label(title);
        titleLabel.setTextFill(Color.web("#080c53"));
        titleLabel.setFont(Font.font("System", 24.0));
        AnchorPane.setTopAnchor(titleLabel, 25.0);
        AnchorPane.setLeftAnchor(titleLabel, 14.0);

        countLabel.setTextFill(Color.web("#00a4bf"));
        countLabel.setFont(Font.font("System", FontWeight.BOLD, 28.0));
        AnchorPane.setTopAnchor(countLabel, 66.0);
        AnchorPane.setLeftAnchor(countLabel, 14.0);
        
        // Icon Circle Placeholder
        AnchorPane iconCircle = new AnchorPane();
        iconCircle.setPrefHeight(80.0);
        iconCircle.setPrefWidth(80.0);
        iconCircle.setStyle("-fx-background-color: rgba(0, 164, 191, 0.1); -fx-background-radius: 40;");
        AnchorPane.setTopAnchor(iconCircle, 20.0);
        AnchorPane.setRightAnchor(iconCircle, 20.0);

        box.getChildren().addAll(titleLabel, countLabel, iconCircle);
        return box;
    }
}