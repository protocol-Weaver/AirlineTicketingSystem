package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.View;
import artifact.Backend.Controller.AircraftsController;
import artifact.Backend.Models.Aircraft;
import artifact.Backend.Models.DTO.AircraftRequest;
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
 * Represents the view for managing aircrafts.
 * This view allows users to add new aircrafts and see a list of existing aircrafts.
 */
public class AircraftsView extends AnchorPane {

    private final AircraftsController controller;

    // UI Components
    private final TextField typeField = new TextField();
    private final TextField capacityField = new TextField();
    private final Label typeRequired = new Label();
    private final Label capacityRequired = new Label();
    private final TableView<Aircraft> airCraftsTable = new TableView<>();

    /**
     * Constructs the AircraftsView.
     * Initializes the UI components and sets up the layout and event handlers.
     */
    public AircraftsView() {
        this.controller = new AircraftsController();

        setPrefHeight(560.0);
        setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.AIRCRAFTS);

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

        Label formTitle = new Label("Add Aircraft");
        formTitle.setTextFill(Color.web("#080c53"));
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(formTitle, 14.0);
        AnchorPane.setLeftAnchor(formTitle, 186.0);

        // Type Field
        Label typeLabel = new Label("Aircraft Type*");
        AnchorPane.setTopAnchor(typeLabel, 58.0); AnchorPane.setLeftAnchor(typeLabel, 12.0);
        
        typeField.setPromptText("Enter Aircraft Type");
        typeField.setPrefHeight(40.0);
        typeField.setPrefWidth(260.0);
        AnchorPane.setTopAnchor(typeField, 80.0); AnchorPane.setLeftAnchor(typeField, 12.0);

        typeRequired.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(typeRequired, 120.0); AnchorPane.setLeftAnchor(typeRequired, 12.0);
        
        // Capacity Field
        Label capacityLabel = new Label("Capacity*");
        AnchorPane.setTopAnchor(capacityLabel, 58.0); AnchorPane.setLeftAnchor(capacityLabel, 284.0);
        
        capacityField.setPromptText("Enter Capacity");
        capacityField.setPrefHeight(40.0);
        capacityField.setPrefWidth(224.0);
        AnchorPane.setTopAnchor(capacityField, 80.0); AnchorPane.setLeftAnchor(capacityField, 284.0);
        
        capacityRequired.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(capacityRequired, 120.0); AnchorPane.setLeftAnchor(capacityRequired, 284.0);

        // Add Button
        Button addButton = new Button("Add");
        addButton.setPrefHeight(40.0);
        addButton.setPrefWidth(100.0);
        addButton.setStyle("-fx-background-color: #00a4bf;");
        addButton.setTextFill(Color.WHITE);
        addButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        addButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(addButton, 138.0);
        AnchorPane.setRightAnchor(addButton, 12.0);
        AnchorPane.setBottomAnchor(addButton, 12.0);

        formPane.getChildren().addAll(
            formTitle, typeLabel, typeField, typeRequired,
            capacityLabel, capacityField, capacityRequired, addButton
        );
        
        // --- Table View ---
        Label tableTitle = new Label("Aircrafts");
        tableTitle.setTextFill(Color.web("#080c53"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(tableTitle, 223.0);
        AnchorPane.setLeftAnchor(tableTitle, 15.0);

        AnchorPane.setTopAnchor(airCraftsTable, 267.0);
        AnchorPane.setLeftAnchor(airCraftsTable, 15.0);
        AnchorPane.setRightAnchor(airCraftsTable, 15.0);
        AnchorPane.setBottomAnchor(airCraftsTable, 15.0);
        
        mainContent.getChildren().addAll(formPane, tableTitle, airCraftsTable);
        getChildren().addAll(sidebar, mainContent);

        // --- Bind Actions ---
        
        // 1. Initialize Table
        controller.initialize(airCraftsTable);

        // 2. Add Button Action
        addButton.setOnAction(e -> handleAddAction());
    }

    /**
     * Handles the action of adding a new aircraft.
     * It retrieves the data from the form, sends it to the controller,
     * and displays feedback to the user based on the result.
     */
    private void handleAddAction() {
        // 1. Clear Errors
        typeRequired.setText("");
        capacityRequired.setText("");

        // 2. Create DTO
        AircraftRequest request = new AircraftRequest(
            typeField.getText(),
            capacityField.getText()
        );

        // 3. Call Controller
        ServiceResult result = controller.addAircraft(request);

        // 4. Handle Result
        if (result.isSuccess()) {
            AlertUtils.infoBox("Aircraft Added Successfully", "Success");
            typeField.clear();
            capacityField.clear();
        } else {
            // Global Error
            if (result.getGlobalError() != null) {
                AlertUtils.errorBox(result.getGlobalError(), "Error");
            }
            // Field Errors
            typeRequired.setText(result.getFieldError("type"));
            capacityRequired.setText(result.getFieldError("capacity"));
        }
    }
}