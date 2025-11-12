package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.Controller.ReservationsController;
import artifact.Backend.Models.DTO.ReservationRequest;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.View;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReservationsView extends AnchorPane {

    private final ReservationsController controller;

    // --- Form UI Components ---
    private final TextField customerName = new TextField();
    private final TextField customerPhone = new TextField();
    private final ComboBox<Flight> flight = new ComboBox<>();
    private final TextField seatNum = new TextField();
    private final DatePicker resDate = new DatePicker();
    private final TextField price = new TextField();
    private final CheckBox payStatus = new CheckBox(" Paid");
    private final Button addButton = new Button("Add");

    // --- Error Labels ---
    private final Label custNameReq = new Label();
    private final Label custPhoneReq = new Label();
    private final Label flightReq = new Label();
    private final Label seatNumReq = new Label();
    private final Label resDateReq = new Label();
    private final Label priceReq = new Label();

    // --- Table View ---
    private final TableView<Reservation> reservationsTable = new TableView<>();

    public ReservationsView() {
        this.controller = new ReservationsController();

        setPrefHeight(560.0);
        setPrefWidth(750.0);
        setStyle("-fx-background-color: #f4f4f4;");

        // --- Sidebar ---
        SidebarView sidebar = new SidebarView(controller, View.RESERVATIONS);

        // --- Main Content ---
        AnchorPane mainContent = new AnchorPane();
        mainContent.setPrefHeight(560.0);
        mainContent.setPrefWidth(550.0);
        AnchorPane.setTopAnchor(mainContent, 0.0);
        AnchorPane.setLeftAnchor(mainContent, 200.0);
        
        // --- Form Box ---
        AnchorPane formPane = new AnchorPane();
        formPane.setStyle("-fx-background-color: #fff; -fx-background-radius: 4px;");
        formPane.setPrefHeight(348.0);
        AnchorPane.setTopAnchor(formPane, 15.0);
        AnchorPane.setLeftAnchor(formPane, 15.0);
        AnchorPane.setRightAnchor(formPane, 15.0);
        formPane.setPadding(new Insets(10.0));
        
        Label formTitle = new Label("Add Reservation");
        formTitle.setTextFill(Color.web("#080c53"));
        formTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(formTitle, 6.0);
        AnchorPane.setLeftAnchor(formTitle, 167.0);
        
        // Row 1: Customer Name & Phone
        setupLabelAndField(formPane, "Customer Name*", customerName, custNameReq, 41.0, 11.0);
        setupLabelAndField(formPane, "Customer Phone*", customerPhone, custPhoneReq, 41.0, 267.0);

        // Row 2: Flight & Seat
        setupLabelAndField(formPane, "Flight*", flight, flightReq, 124.0, 12.0);
        setupLabelAndField(formPane, "Seat Number*", seatNum, seatNumReq, 124.0, 354.0);
        // Special adjustment for Seat Number width
        seatNum.setPrefWidth(154.0);

        // Row 3: Date & Price
        setupLabelAndField(formPane, "Reservation Date*", resDate, resDateReq, 206.0, 14.0);
        setupLabelAndField(formPane, "Price*", price, priceReq, 206.0, 266.0);

        // Row 4: Paid Status & Add Button
        payStatus.setTextFill(Color.web("#080c53"));
        payStatus.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        AnchorPane.setTopAnchor(payStatus, 301.0);
        AnchorPane.setLeftAnchor(payStatus, 12.0);

        addButton.setPrefHeight(40.0);
        addButton.setPrefWidth(121.0);
        addButton.setStyle("-fx-background-color: #00a4bf;");
        addButton.setTextFill(Color.WHITE);
        addButton.setFont(Font.font("System", FontWeight.BOLD, 14.0));
        addButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(addButton, 291.0);
        AnchorPane.setRightAnchor(addButton, 12.0);

        formPane.getChildren().addAll(
            formTitle, payStatus, addButton,
            customerName, custNameReq,
            customerPhone, custPhoneReq,
            flight, flightReq,
            seatNum, seatNumReq,
            resDate, resDateReq,
            price, priceReq
        );
        // Note: Labels are added via helper, but text fields/error labels needed adding

        // --- Table View ---
        Label tableTitle = new Label("Reservations");
        tableTitle.setTextFill(Color.web("#080c53"));
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 24.0));
        AnchorPane.setTopAnchor(tableTitle, 368.0);
        AnchorPane.setLeftAnchor(tableTitle, 15.0);

        AnchorPane.setTopAnchor(reservationsTable, 408.0);
        AnchorPane.setLeftAnchor(reservationsTable, 15.0);
        AnchorPane.setRightAnchor(reservationsTable, 15.0);
        AnchorPane.setBottomAnchor(reservationsTable, 15.0);
        
        mainContent.getChildren().addAll(formPane, tableTitle, reservationsTable);
        getChildren().addAll(sidebar, mainContent);

        // --- Bind Actions ---
        // 1. Initialize Table
        controller.initialize(reservationsTable, flight);

        // 2. Add Button Logic (The View handles the UI!)
        addButton.setOnAction(e -> handleAddAction());
    }

    private void handleAddAction() {
        // 1. Clear old errors
        clearErrorLabels();

        // 2. Create DTO from UI inputs
        ReservationRequest request = new ReservationRequest(
            customerName.getText(),
            customerPhone.getText(),
            flight.getValue(),
            seatNum.getText(),
            resDate.getValue(),
            price.getText(),
            payStatus.isSelected()
        );

        // 3. Call Controller
        ServiceResult result = controller.addReservation(request);

        // 4. Update UI based on Result
        if (result.isSuccess()) {
            AlertUtils.infoBox("Reservation Created Successfully", "Success");
            clearFormFields();
        } else {
            // Global Error
            if (result.getGlobalError() != null) {
                AlertUtils.errorBox(result.getGlobalError(), "Booking Failed");
            }
            // Field Errors
            custNameReq.setText(result.getFieldError("name"));
            custPhoneReq.setText(result.getFieldError("phone"));
            flightReq.setText(result.getFieldError("flight"));
            priceReq.setText(result.getFieldError("price"));
            seatNumReq.setText(result.getFieldError("seat"));
        }
    }

    // --- Helpers ---

    private void clearErrorLabels() {
        custNameReq.setText("");
        custPhoneReq.setText("");
        flightReq.setText("");
        seatNumReq.setText("");
        resDateReq.setText("");
        priceReq.setText("");
    }

    private void clearFormFields() {
        customerName.clear();
        customerPhone.clear();
        flight.setValue(null);
        seatNum.clear();
        resDate.setValue(null);
        price.clear();
        payStatus.setSelected(false);
    }

    // Helper to position a Label, InputField, and ErrorLabel together
    private void setupLabelAndField(AnchorPane pane, String labelText, Control input, Label errLabel, double topY, double leftX) {
        // Label
        Label title = new Label(labelText);
        AnchorPane.setTopAnchor(title, topY);
        AnchorPane.setLeftAnchor(title, leftX);
        pane.getChildren().add(title);

        // Input Field (TextField, DatePicker, or ComboBox)
        input.setPrefHeight(40.0);
        if (!(input instanceof TextField) || input != seatNum) { 
             // Default width for most fields, SeatNum overrides this manually later
             input.setPrefWidth(input == flight ? 330.0 : 242.0); 
        }
        
        // Adjust Input Y position (approx 20px below label)
        AnchorPane.setTopAnchor(input, topY + 20.0);
        AnchorPane.setLeftAnchor(input, leftX);

        // Error Label
        errLabel.setTextFill(Color.RED);
        errLabel.setPrefHeight(18.0);
        AnchorPane.setTopAnchor(errLabel, topY + 62.0); // Below input
        AnchorPane.setLeftAnchor(errLabel, leftX);
    }
}