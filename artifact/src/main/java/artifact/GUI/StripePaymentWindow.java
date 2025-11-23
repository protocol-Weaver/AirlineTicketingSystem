package artifact.GUI;

import artifact.Backend.Services.Impl.StripeService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class StripePaymentWindow {

    private final StripeService stripeService;

    public StripePaymentWindow() {
        this.stripeService = new StripeService();
    }

    public void open(double amount, Consumer<String> onPaymentSuccess) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Stripe Secure Payment (Test Mode)");
        window.setResizable(false);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: white; -fx-font-family: 'Segoe UI', sans-serif;");
        layout.setAlignment(Pos.CENTER_LEFT);

        Label header = new Label("Pay with Card");
        header.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label subHeader = new Label(String.format("Total: PKR %,.2f", amount));

        // --- Fields (Pre-filled for Testing) ---
        // We pre-fill these so you can just click "Pay" immediately
        
        Label lblCard = new Label("Card Number");
        TextField txtCard = new TextField("4242 4242 4242 4242"); 
        txtCard.setPromptText("Card Number");
        
        Label lblExp = new Label("Expiry / CVC");
        TextField txtExpMonth = new TextField("12"); 
        txtExpMonth.setPromptText("MM"); 
        txtExpMonth.setPrefWidth(50);
        
        TextField txtExpYear = new TextField("2025"); 
        txtExpYear.setPromptText("YYYY"); 
        txtExpYear.setPrefWidth(70);
        
        TextField txtCvc = new TextField("123"); 
        txtCvc.setPromptText("CVC"); 
        txtCvc.setPrefWidth(60);

        HBox expiryBox = new HBox(10, txtExpMonth, new Label("/"), txtExpYear, new Label("  "), txtCvc);
        expiryBox.setAlignment(Pos.CENTER_LEFT);

        Button btnPay = new Button("Pay PKR " + String.format("%,.0f", amount));
        btnPay.setMaxWidth(Double.MAX_VALUE);
        btnPay.setStyle("-fx-background-color: #635bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");

        Label lblStatus = new Label("Test Mode: Credentials Auto-filled");
        lblStatus.setStyle("-fx-text-fill: #666; -fx-font-size: 10px;");

        // --- Action ---
        btnPay.setOnAction(e -> {
            lblStatus.setText("Processing payment...");
            lblStatus.setStyle("-fx-text-fill: blue;");
            btnPay.setDisable(true);

            // Run API call in background thread to avoid freezing UI
            Task<String> paymentTask = new Task<>() {
                @Override
                protected String call() throws Exception {
                    // PARSE INPUTS
                    String card = txtCard.getText().trim();
                    String cvc = txtCvc.getText().trim();
                    
                    // Simple validation for empty fields
                    if(card.isEmpty() || txtExpMonth.getText().isEmpty() || txtExpYear.getText().isEmpty()) {
                        throw new Exception("Please fill all fields");
                    }

                    int month = Integer.parseInt(txtExpMonth.getText().trim());
                    int year = Integer.parseInt(txtExpYear.getText().trim());

                    // CALL REAL STRIPE API
                    return stripeService.chargeCreditCard(card, month, year, cvc, amount);
                }
            };

            paymentTask.setOnSucceeded(event -> {
                String txnId = paymentTask.getValue();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Payment Successful!\nTransaction ID: " + txnId);
                alert.showAndWait();
                window.close();
                if (onPaymentSuccess != null) onPaymentSuccess.accept(txnId);
            });

            paymentTask.setOnFailed(event -> {
                Throwable error = paymentTask.getException();
                lblStatus.setText("Failed: " + error.getMessage());
                lblStatus.setStyle("-fx-text-fill: red;");
                btnPay.setDisable(false);
                error.printStackTrace(); 
            });

            new Thread(paymentTask).start();
        });

        layout.getChildren().addAll(
            header, subHeader, new Separator(), 
            lblCard, txtCard, 
            lblExp, expiryBox, 
            new Separator(), 
            lblStatus, btnPay
        );
        
        window.setScene(new Scene(layout, 350, 420));
        window.showAndWait();
    }
}