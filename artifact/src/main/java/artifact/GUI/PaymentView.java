package artifact.GUI;

import artifact.Backend.Controller.PaymentController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PaymentView extends StackPane {

    private final PaymentController controller;
    private final Label flightHeader = new Label("Flight to [City]");
    private final Label flightRoute = new Label("[AAA] ➔ [BBB]");
    private final Label flightDate = new Label("[Date]");
    private final Label flightTime = new Label("[Time]");
    private final Label flightPassenger = new Label("1 Adult, Economy");
    private final Label baseFare = new Label("PKR 0.00");
    private final Label taxes = new Label("PKR 0.00");
    private final Label totalPrice = new Label("PKR 0.00");

    public PaymentView() {
        this.controller = new PaymentController();

        try {
            this.getStylesheets().add(getClass().getResource("/Styles/PaymentStyles.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load PaymentStyles.css.");
            this.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-background-color: #f4f4f4;");
        }

        setPadding(new Insets(32.0));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(createHeader());

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("main-scroll-pane");

        VBox contentLayout = new VBox(32.0);
        contentLayout.setPadding(new Insets(0, 0, 32, 0));

        contentLayout.getChildren().add(createOrderSummary());

        scrollPane.setContent(contentLayout);
        borderPane.setCenter(scrollPane);
        getChildren().add(borderPane);

        controller.initialize(
            flightHeader, flightRoute, flightDate, flightTime,
            flightPassenger, baseFare, taxes, totalPrice
        );
    }

    private HBox createHeader() {
        HBox header = new HBox(16.0);
        header.setAlignment(Pos.CENTER_LEFT);
        Label headerLabel = new Label("Complete Your Booking");
        headerLabel.getStyleClass().add("header-label");
        header.getChildren().add(headerLabel);
        return header;
    }

    private VBox createOrderSummary() {
        VBox box = new VBox(16.0);

        Label title = new Label("Order Summary");
        title.getStyleClass().add("card-header");

        // Flight Info
        VBox flightInfo = new VBox(4.0);
        flightInfo.getStyleClass().add("summary-section");
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        headerRow.getChildren().addAll(flightHeader, spacer, flightRoute);
        flightDate.getStyleClass().add("summary-text-light");
        flightTime.getStyleClass().add("summary-text-light");
        flightPassenger.getStyleClass().add("summary-text-light");
        flightInfo.getChildren().addAll(headerRow, flightDate, flightTime, flightPassenger);

        // Price Breakdown
        VBox prices = new VBox(8.0);
        prices.getStyleClass().add("summary-section");
        prices.getChildren().addAll(
            createPriceRow("Base Fare", baseFare, "summary-text-light", "summary-text-dark"),
            createPriceRow("Taxes & Fees", taxes, "summary-text-light", "summary-text-dark"),
            createPriceRow("Baggage Fee", new Label("PKR 0.00"), "summary-text-light", "summary-text-dark")
        );

        // Total
        VBox totalSection = new VBox(4.0);
        totalSection.getStyleClass().add("summary-section");
        totalSection.setAlignment(Pos.TOP_RIGHT);
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label totalLabel = new Label("Total");
        totalLabel.getStyleClass().add("total-label");
        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalPrice.getStyleClass().add("total-price-label");
        totalRow.getChildren().addAll(totalLabel, spacer2, totalPrice);
        Label currency = new Label("PKR");
        currency.getStyleClass().add("summary-text-light");
        totalSection.getChildren().addAll(totalRow, currency);

        // --- UPDATED PAYMENT BUTTONS ---
        
        // Button 1: Pay with Stripe (Primary)
        Button payWithStripe = new Button("Pay Now (Credit/Debit Card)");
        payWithStripe.setMaxWidth(Double.MAX_VALUE);
        // Changed color to Stripe's "Blurple" (#635bff)
        payWithStripe.setStyle("-fx-background-color: #635bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-font-size: 16px; -fx-pref-height: 45px;");
        payWithStripe.setCursor(Cursor.HAND);

        // Button 2: Pay Later (Secondary)
        Button payLater = new Button("Pay Later");
        payLater.setMaxWidth(Double.MAX_VALUE);
        payLater.setStyle("-fx-background-color: #eee; -fx-text-fill: #333; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-font-size: 16px; -fx-pref-height: 45px;");
        payLater.setCursor(Cursor.HAND);
        
        // Optional: Keep JazzCash as a secondary option if desired, or remove it from UI.
        // For now, I have replaced the JazzCash button with Stripe as requested, 
        // but the Controller still has the logic if you ever want to add a third button.

        VBox paymentOptions = new VBox(10, payWithStripe, payLater);

        Text terms = new Text("By confirming, I agree to the Terms of Service. 'Pay Later' bookings will expire 24 hours before departure.");
        terms.setWrappingWidth(300.0);
        terms.setTextAlignment(TextAlignment.CENTER);
        terms.setStyle("-fx-fill: #777;");

        box.getChildren().addAll(title, flightInfo, new Separator(), prices, new Separator(), totalSection, paymentOptions, new StackPane(terms));

        // Bind actions
        payWithStripe.setOnAction(e -> controller.handlePayNow()); // This now triggers Stripe
        payLater.setOnAction(e -> controller.handlePayLater());

        return box;
    }

    private HBox createPriceRow(String labelText, Label priceLabel, String labelClass, String priceClass) {
        HBox row = new HBox();
        Label label = new Label(labelText);
        label.getStyleClass().add(labelClass);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        priceLabel.getStyleClass().add(priceClass);
        row.getChildren().addAll(label, spacer, priceLabel);
        return row;
    }
}