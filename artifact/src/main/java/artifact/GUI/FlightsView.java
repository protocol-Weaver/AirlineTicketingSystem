package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.View;
import artifact.Backend.Controller.FlightsController;
import artifact.Backend.Models.Aircraft;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.Crew;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.DTO.FlightRequest;
import artifact.Backend.Models.ServiceResult;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FlightsView extends AnchorPane {

    private final FlightsController controller;

    // Define components
    private final ComboBox<Airport> depAirports = new ComboBox<>();
    private final ComboBox<Airport> arrAirports = new ComboBox<>();
    private final ComboBox<Aircraft> airCraftsNames = new ComboBox<>();
    private final ComboBox<Crew> crewNames = new ComboBox<>();
    private final DatePicker depTime = new DatePicker();
    private final DatePicker arrTime = new DatePicker();
    
    // Labels
    private final Label seats = new Label("0");
    private final Label depAirportSelected = new Label();
    private final Label arrAirportSelected = new Label();
    private final Label aircraftSelected = new Label();
    private final Label crewSelected = new Label();
    private final Label depTimeRequired = new Label();
    private final Label arrTimeRequired = new Label();
    
    private final TableView<Flight> flightsTable = new TableView<>();

    public FlightsView() {
        this.controller = new FlightsController();
        setPrefHeight(560.0); setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.FLIGHTS);

        // --- Main Content ---
        AnchorPane mainContent = new AnchorPane();
        mainContent.setPrefHeight(560.0); mainContent.setPrefWidth(550.0);
        AnchorPane.setTopAnchor(mainContent, 0.0); AnchorPane.setLeftAnchor(mainContent, 200.0);

        // --- Form Box ---
        AnchorPane formPane = new AnchorPane();
        formPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 4px;");
        AnchorPane.setTopAnchor(formPane, 15.0); AnchorPane.setLeftAnchor(formPane, 15.0); AnchorPane.setRightAnchor(formPane, 15.0);
        formPane.setPadding(new Insets(10.0));

        Label formTitle = new Label("Add Flight");
        formTitle.setTextFill(Color.web("#080c53"));
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(formTitle, 8.0); AnchorPane.setLeftAnchor(formTitle, 195.0);

        // UI Setup helpers
        setupField(formPane, "Departure Airport*", depAirports, depAirportSelected, 57.0, 11.0);
        setupField(formPane, "Arrival Airport*", arrAirports, arrAirportSelected, 57.0, 266.0);
        setupField(formPane, "Departure Time*", depTime, depTimeRequired, 143.0, 11.0);
        setupField(formPane, "Arrival Time*", arrTime, arrTimeRequired, 143.0, 266.0);
        setupField(formPane, "Aircraft*", airCraftsNames, aircraftSelected, 229.0, 11.0);
        setupField(formPane, "Crew*", crewNames, crewSelected, 229.0, 266.0);

        // Available Seats Display
        Label availableSeatsLabel = new Label("Available Seats:");
        availableSeatsLabel.setFont(Font.font("System", FontWeight.BOLD, 12.0));
        AnchorPane.setTopAnchor(availableSeatsLabel, 318.0); AnchorPane.setLeftAnchor(availableSeatsLabel, 11.0);
        
        seats.setTextFill(Color.web("#080c53"));
        seats.setFont(Font.font("System", FontWeight.BOLD, 18.0));
        AnchorPane.setTopAnchor(seats, 313.0); AnchorPane.setLeftAnchor(seats, 110.0);

        Button addButton = new Button("Add");
        addButton.setPrefHeight(40.0); addButton.setPrefWidth(100.0);
        addButton.setStyle("-fx-background-color: #00a4bf;");
        addButton.setTextFill(Color.WHITE);
        addButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        addButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(addButton, 306.0); AnchorPane.setRightAnchor(addButton, 12.0);
        AnchorPane.setBottomAnchor(addButton, 12.0);
        
        formPane.getChildren().addAll(formTitle, availableSeatsLabel, seats, addButton);

        // --- Table View ---
        Label tableTitle = new Label("Flights");
        tableTitle.setTextFill(Color.web("#080c53"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(tableTitle, 389.0); AnchorPane.setLeftAnchor(tableTitle, 15.0);

        AnchorPane.setTopAnchor(flightsTable, 432.0); AnchorPane.setLeftAnchor(flightsTable, 15.0);
        AnchorPane.setRightAnchor(flightsTable, 15.0); AnchorPane.setBottomAnchor(flightsTable, 15.0);

        mainContent.getChildren().addAll(formPane, tableTitle, flightsTable);
        getChildren().addAll(sidebar, mainContent);

        // --- Bind Actions ---
        controller.initialize(flightsTable, depAirports, arrAirports, airCraftsNames, crewNames);

        airCraftsNames.setOnAction(e -> controller.onAircraftSelected(airCraftsNames.getValue(), seats));
        
        addButton.setOnAction(e -> handleAddAction());
    }

    private void handleAddAction() {
        // 1. Clear Errors
        depAirportSelected.setText(""); arrAirportSelected.setText("");
        aircraftSelected.setText(""); crewSelected.setText("");
        depTimeRequired.setText(""); arrTimeRequired.setText("");

        // 2. Create DTO
        FlightRequest request = new FlightRequest(
            depAirports.getValue(), arrAirports.getValue(),
            airCraftsNames.getValue(), crewNames.getValue(),
            depTime.getValue(), arrTime.getValue()
        );

        // 3. Call Controller
        ServiceResult result = controller.addFlight(request);

        // 4. Handle Result
        if (result.isSuccess()) {
            AlertUtils.infoBox("Flight Added Successfully", "Success");
            clearForm();
        } else {
            if (result.getGlobalError() != null) AlertUtils.errorBox(result.getGlobalError(), "Error");
            
            depAirportSelected.setText(result.getFieldError("depAirport"));
            arrAirportSelected.setText(result.getFieldError("arrAirport"));
            aircraftSelected.setText(result.getFieldError("aircraft"));
            crewSelected.setText(result.getFieldError("crew"));
            depTimeRequired.setText(result.getFieldError("depDate"));
            arrTimeRequired.setText(result.getFieldError("arrDate"));
        }
    }

    private void clearForm() {
        depAirports.setValue(null); arrAirports.setValue(null);
        airCraftsNames.setValue(null); crewNames.setValue(null);
        depTime.setValue(null); arrTime.setValue(null);
        seats.setText("0");
    }

    // Helper to reduce boilerplate layout code
    private void setupField(AnchorPane pane, String labelText, Control input, Label errLabel, double y, double x) {
        Label lbl = new Label(labelText);
        AnchorPane.setTopAnchor(lbl, y); AnchorPane.setLeftAnchor(lbl, x);
        
        input.setPrefHeight(40.0); input.setPrefWidth(242.0);
        if (input instanceof ComboBox) ((ComboBox<?>)input).setStyle("-fx-background-color: #f4f4f4;");
        AnchorPane.setTopAnchor(input, y + 21.0); AnchorPane.setLeftAnchor(input, x);
        
        errLabel.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(errLabel, y + 61.0); AnchorPane.setLeftAnchor(errLabel, x);
        
        pane.getChildren().addAll(lbl, input, errLabel);
    }
}