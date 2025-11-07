package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.View;
import artifact.Backend.Controller.AirportsController;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.DTO.AirportRequest;
import artifact.Backend.Models.ServiceResult;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Represents the view for managing airports.
 * This view allows users to add new airports and see a list of existing airports.
 */
public class AirportsView extends AnchorPane {

    private final AirportsController controller;
    
    // UI Components defined here (Correct)
    private final TextField airPortNameField = new TextField();
    private final TextField airPortLocationField = new TextField();
    private final Label nameRequiredLabel = new Label();
    private final Label locationRequiredLabel = new Label();
    private final TableView<Airport> airportsTable = new TableView<>();

    /**
     * Constructs the AirportsView.
     * Initializes the UI components and sets up the layout and event handlers.
     */
    public AirportsView() {
        this.controller = new AirportsController();

        setPrefHeight(560.0);
        setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.AIRPORTS);

        // --- Main Content ---
        AnchorPane mainContent = new AnchorPane();
        mainContent.setPrefHeight(560.0);
        mainContent.setPrefWidth(550.0);
        AnchorPane.setTopAnchor(mainContent, 0.0);
        AnchorPane.setLeftAnchor(mainContent, 200.0);
        
        // --- Form Box ---
        AnchorPane formPane = new AnchorPane();
        formPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 4px;");
        AnchorPane.setTopAnchor(formPane, 15.0);
        AnchorPane.setLeftAnchor(formPane, 15.0);
        AnchorPane.setRightAnchor(formPane, 15.0);
        formPane.setPadding(new Insets(10.0));

        Label formTitle = new Label("Add Airport");
        formTitle.setTextFill(Color.web("#080c53"));
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(formTitle, 12.0);
        AnchorPane.setLeftAnchor(formTitle, 195.0);

        // Name Field
        Label nameLabel = new Label("Airport Name*");
        AnchorPane.setTopAnchor(nameLabel, 41.0); AnchorPane.setLeftAnchor(nameLabel, 12.0);

        airPortNameField.setPromptText("Enter Airport Name");
        airPortNameField.setPrefHeight(40.0);
        AnchorPane.setTopAnchor(airPortNameField, 58.0);
        AnchorPane.setLeftAnchor(airPortNameField, 12.0);
        AnchorPane.setRightAnchor(airPortNameField, 12.0);
        
        nameRequiredLabel.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(nameRequiredLabel, 98.0); AnchorPane.setLeftAnchor(nameRequiredLabel, 14.0);
        
        // Location Field
        Label locationLabel = new Label("Airport Location*");
        AnchorPane.setTopAnchor(locationLabel, 125.0); AnchorPane.setLeftAnchor(locationLabel, 12.0);
        
        airPortLocationField.setPromptText("Enter Airport Location");
        airPortLocationField.setPrefHeight(40.0);
        AnchorPane.setTopAnchor(airPortLocationField, 144.0);
        AnchorPane.setLeftAnchor(airPortLocationField, 11.0);
        AnchorPane.setRightAnchor(airPortLocationField, 12.0);

        locationRequiredLabel.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(locationRequiredLabel, 184.0); AnchorPane.setLeftAnchor(locationRequiredLabel, 11.0);
        
        // Add Button
        Button addButton = new Button("Add");
        addButton.setPrefHeight(40.0);
        addButton.setPrefWidth(100.0);
        addButton.setStyle("-fx-background-color: #00a4bf;");
        addButton.setTextFill(Color.WHITE);
        addButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        addButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(addButton, 201.0);
        AnchorPane.setRightAnchor(addButton, 12.0);
        AnchorPane.setBottomAnchor(addButton, 12.0);

        formPane.getChildren().addAll(
            formTitle, nameLabel, airPortNameField, nameRequiredLabel,
            locationLabel, airPortLocationField, locationRequiredLabel, addButton
        );

        // --- Table View ---
        Label tableTitle = new Label("Airports");
        tableTitle.setTextFill(Color.web("#080c53"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(tableTitle, 280.0);
        AnchorPane.setLeftAnchor(tableTitle, 15.0);

        AnchorPane.setTopAnchor(airportsTable, 321.0);
        AnchorPane.setLeftAnchor(airportsTable, 15.0);
        AnchorPane.setRightAnchor(airportsTable, 15.0);
        AnchorPane.setBottomAnchor(airportsTable, 15.0);
        
        mainContent.getChildren().addAll(formPane, tableTitle, airportsTable);
        getChildren().addAll(sidebar, mainContent);

        // --- Bind Actions ---
        
        // 1. Initialize Table
        controller.initialize(airportsTable);

        // 2. Add Button Action
        addButton.setOnAction(e -> handleAddAction());
    }

    /**
     * Handles the action of adding a new airport.
     * It retrieves the data from the form, sends it to the controller,
     * and displays feedback to the user based on the result.
     */
    private void handleAddAction() {
        // 1. Clear Errors
        nameRequiredLabel.setText("");
        locationRequiredLabel.setText("");

        // 2. Create DTO
        AirportRequest request = new AirportRequest(
            airPortNameField.getText(),
            airPortLocationField.getText()
        );

        // 3. Call Controller
        ServiceResult result = controller.addAirport(request);

        // 4. Handle Result
        if (result.isSuccess()) {
            AlertUtils.infoBox("Airport Added Successfully", "Success");
            airPortNameField.clear();
            airPortLocationField.clear();
            // Note: Since the Repo and Table share the same ObservableList, the table updates automatically
        } else {
            // Handle Global Error
            if (result.getGlobalError() != null) {
                AlertUtils.errorBox(result.getGlobalError(), "Error");
            }
            // Handle Field Errors
            nameRequiredLabel.setText(result.getFieldError("name"));
            locationRequiredLabel.setText(result.getFieldError("location"));
        }
    }
}