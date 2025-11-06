package artifact.GUI;

import artifact.Backend.AlertUtils;
import artifact.Backend.Controller.MyBookingsController;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Tags.BookingStatus;
import artifact.Backend.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Base64;
import java.util.Optional;

public class MyBookingsView extends BorderPane {

    private final MyBookingsController controller;
    private final TableView<Ticket> ticketsTable = new TableView<>();

    // Color Palette
    private static final String BACKGROUND_COLOR = "#F4F6F8";
    private static final String CARD_COLOR = "#FFFFFF";
    private static final String PRIMARY_COLOR = "#00A4BF";
    private static final String PRIMARY_HOVER_COLOR = "#008C9E";
    private static final String TEXT_HEADER_COLOR = "#1A202C";
    private static final String TEXT_BODY_COLOR = "#4A5568";

    public MyBookingsView() {
        this.controller = new MyBookingsController();
        
        // 1. Global View Styling
        setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        // Inject custom CSS for advanced TableView styling (Headers, Rows)
        getStylesheets().add(getInlineStyleSheet());

        // 2. Navbar
        setTop(new UserNavbarView(controller, View.MY_BOOKINGS));

        // 3. Main Content Wrapper
        VBox centerContent = new VBox(25.0);
        centerContent.setPadding(new Insets(40.0, 60.0, 40.0, 60.0));
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setMaxWidth(1200); // Prevent stretching too wide on huge screens

        // 4. Header Section
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("My Bookings");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28.0));
        title.setTextFill(Color.web(TEXT_HEADER_COLOR));

        Label subtitle = new Label("Manage your active tickets and pending payments.");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14.0));
        subtitle.setTextFill(Color.web(TEXT_BODY_COLOR));
        
        headerBox.getChildren().addAll(title, subtitle);

        // 5. Card Container for Table
        StackPane tableCard = new StackPane();
        tableCard.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 12px;");
        tableCard.setPadding(new Insets(0)); // Padding handled by table
        
        // Soft Shadow Effect for the Card
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(20);
        shadow.setOffsetY(5);
        shadow.setBlurType(BlurType.GAUSSIAN);
        tableCard.setEffect(shadow);

        // 6. Table Configuration
        setupTableStyling();
        
        // Add table to card
        tableCard.getChildren().add(ticketsTable);

        // Pass the action column creation logic
        TableColumn<Ticket, Void> actionCol = createActionColumn();
        
        // 7. Assemble
        // Ensure the card expands
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        centerContent.getChildren().addAll(headerBox, tableCard);
        
        setCenter(centerContent);

        controller.initialize(ticketsTable, actionCol);
    }

    private void setupTableStyling() {
        ticketsTable.setPlaceholder(new Label("You have no active bookings."));
        // Remove default border and make background match card
        ticketsTable.setStyle("-fx-background-color: transparent; -fx-background-radius: 12px;");
        ticketsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private TableColumn<Ticket, Void> createActionColumn() {
        TableColumn<Ticket, Void> actionCol = new TableColumn<>("Action");
        actionCol.setSortable(false);
        // Center the header
        actionCol.setStyle("-fx-alignment: CENTER;");
        
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button payButton = new Button("Pay Now");
            
            {
                // Modern Pill Button Styling
                payButton.setStyle(
                    "-fx-background-color: " + PRIMARY_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 20px; " +
                    "-fx-padding: 6 16 6 16;"
                );
                payButton.setCursor(javafx.scene.Cursor.HAND);
                
                // Hover Effects
                payButton.setOnMouseEntered(e -> payButton.setStyle(
                    "-fx-background-color: " + PRIMARY_HOVER_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 20px; " +
                    "-fx-padding: 6 16 6 16;"
                ));
                
                payButton.setOnMouseExited(e -> payButton.setStyle(
                    "-fx-background-color: " + PRIMARY_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-family: 'Segoe UI'; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 20px; " +
                    "-fx-padding: 6 16 6 16;"
                ));
                
                // ACTION LOGIC IS HERE IN THE VIEW (Preserved)
                payButton.setOnAction(event -> handlePayAction(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    if (ticket.paymentStatus() == BookingStatus.PENDING) {
                        setGraphic(payButton);
                        setAlignment(Pos.CENTER);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        return actionCol;
    }

    private void handlePayAction(Ticket ticket) {
        Reservation reservation = controller.getReservationForTicket(ticket);
        
        if (reservation == null) {
            AlertUtils.errorBox("Could not find original reservation details.", "Error");
            return;
        }

        // --- UPDATED TO USE STRIPE WINDOW ---
        try {
            StripePaymentWindow stripe = new StripePaymentWindow();
            
            // Open the window with the price and a callback for success
            stripe.open(reservation.price(), (txnId) -> {
                
                // This code only runs if Stripe returns a successful transaction ID
                ServiceResult payResult = controller.processPayment(ticket, reservation);
                
                if (payResult.isSuccess()) {
                    AlertUtils.infoBox("Payment Successful!\nTransaction Ref: " + txnId, "Payment Confirmed");
                    ticketsTable.refresh();
                } else {
                    AlertUtils.errorBox(payResult.getGlobalError(), "Backend Error");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.errorBox("Failed to initialize payment window.", "System Error");
        }
    }

    /**
     * Generates a Data URI for CSS to style internal TableView components
     * nicely without requiring an external file.
     */
    private String getInlineStyleSheet() {
        String css = 
            ".table-view {" +
            "    -fx-background-color: transparent;" +
            "    -fx-base: transparent;" +
            "    -fx-control-inner-background: transparent;" +
            "    -fx-table-cell-border-color: transparent;" +
            "    -fx-table-header-border-color: transparent;" +
            "    -fx-padding: 10;" +
            "}" +
            // Modern Header
            ".table-view .column-header-background {" +
            "    -fx-background-color: transparent;" +
            "    -fx-background-radius: 12px 12px 0 0;" +
            "}" +
            ".table-view .column-header {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-width: 0 0 1 0;" +
            "    -fx-border-color: #E2E8F0;" +
            "    -fx-padding: 15 10 15 10;" +
            "}" +
            ".table-view .column-header .label {" +
            "    -fx-font-family: 'Segoe UI';" +
            "    -fx-font-weight: bold;" +
            "    -fx-text-fill: #64748B;" +
            "    -fx-font-size: 13px;" +
            "}" +
            // Row Styling
            ".table-row-cell {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-width: 0 0 1 0;" +
            "    -fx-border-color: #F1F5F9;" +
            "    -fx-padding: 0 0 0 0;" +
            "}" +
            ".table-row-cell:odd {" +
            "    -fx-background-color: transparent;" +
            "}" +
            ".table-row-cell:hover {" +
            "    -fx-background-color: #F8FAFC;" +
            "}" +
            ".table-row-cell:selected {" +
            "    -fx-background-color: #F1F5F9;" +
            "    -fx-background-insets: 0;" +
            "}" +
            // Cell Text
            ".table-cell {" +
            "    -fx-text-fill: #334155;" +
            "    -fx-font-size: 14px;" +
            "    -fx-alignment: CENTER-LEFT;" +
            "    -fx-padding: 15 10 15 10;" +
            "}" +
            // Hide the empty table column header button
            ".table-view .filler {" +
            "    -fx-background-color: transparent;" +
            "    -fx-border-width: 0 0 1 0;" +
            "    -fx-border-color: #E2E8F0;" +
            "}";

        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes());
    }
}