package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.View;
import artifact.Backend.Controller.CrewsController;
import artifact.Backend.Models.Crew;
import artifact.Backend.Models.DTO.CrewRequest;
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

public class CrewsView extends AnchorPane {

    private final CrewsController controller;

    // UI Components
    private final TextField crewNameField = new TextField();
    private final TextField captainNameField = new TextField();
    private final Label crewRequired = new Label();
    private final Label captainRequired = new Label();
    private final TableView<Crew> crewTable = new TableView<>();

    public CrewsView() {
        this.controller = new CrewsController();

        setPrefHeight(560.0);
        setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.CREWS);

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

        Label formTitle = new Label("Add Crew");
        formTitle.setTextFill(Color.web("#080c53"));
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(formTitle, 14.0);
        AnchorPane.setLeftAnchor(formTitle, 199.0);

        // Crew Name Field
        Label crewLabel = new Label("Crew Name*");
        AnchorPane.setTopAnchor(crewLabel, 58.0); AnchorPane.setLeftAnchor(crewLabel, 12.0);

        crewNameField.setPromptText("Enter crew name");
        crewNameField.setPrefHeight(40.0);
        crewNameField.setPrefWidth(242.0);
        AnchorPane.setTopAnchor(crewNameField, 80.0); AnchorPane.setLeftAnchor(crewNameField, 12.0);

        crewRequired.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(crewRequired, 120.0); AnchorPane.setLeftAnchor(crewRequired, 12.0);
        
        // Captain Name Field
        Label captainLabel = new Label("Captain Name*");
        AnchorPane.setTopAnchor(captainLabel, 58.0); AnchorPane.setLeftAnchor(captainLabel, 266.0);

        captainNameField.setPromptText("Enter captain name");
        captainNameField.setPrefHeight(40.0);
        captainNameField.setPrefWidth(242.0);
        AnchorPane.setTopAnchor(captainNameField, 80.0); AnchorPane.setLeftAnchor(captainNameField, 266.0);
        
        captainRequired.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(captainRequired, 120.0); AnchorPane.setLeftAnchor(captainRequired, 266.0);
        
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
            formTitle, crewLabel, crewNameField, crewRequired,
            captainLabel, captainNameField, captainRequired, addButton
        );
        
        // --- Table View ---
        Label tableTitle = new Label("Crews");
        tableTitle.setTextFill(Color.web("#080c53"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(tableTitle, 221.0);
        AnchorPane.setLeftAnchor(tableTitle, 15.0);

        AnchorPane.setTopAnchor(crewTable, 268.0);
        AnchorPane.setLeftAnchor(crewTable, 15.0);
        AnchorPane.setRightAnchor(crewTable, 15.0);
        AnchorPane.setBottomAnchor(crewTable, 15.0);
        
        mainContent.getChildren().addAll(formPane, tableTitle, crewTable);
        getChildren().addAll(sidebar, mainContent);

        // --- Bind Actions ---
        
        // 1. Initialize Table
        controller.initialize(crewTable);

        // 2. Add Button Action
        addButton.setOnAction(e -> handleAddAction());
    }

    private void handleAddAction() {
        // 1. Clear Errors
        crewRequired.setText("");
        captainRequired.setText("");

        // 2. Create DTO
        CrewRequest request = new CrewRequest(
            crewNameField.getText(),
            captainNameField.getText()
        );

        // 3. Call Controller
        ServiceResult result = controller.addCrew(request);

        // 4. Handle Result
        if (result.isSuccess()) {
            AlertUtils.infoBox("Crew Added Successfully", "Success");
            crewNameField.clear();
            captainNameField.clear();
        } else {
            // Global Error
            if (result.getGlobalError() != null) {
                AlertUtils.errorBox(result.getGlobalError(), "Error");
            }
            // Field Errors
            crewRequired.setText(result.getFieldError("crewName"));
            captainRequired.setText(result.getFieldError("captainName"));
        }
    }
}