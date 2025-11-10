package artifact.GUI;
import artifact.Backend.AlertUtils;
import artifact.Backend.Controller.RegisterController;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.DTO.RegisterRequest;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.Optional;

/**
 * UPDATED: Added Google Signup Button.
 */
public class RegisterView extends AnchorPane {

    private final RegisterController controller;
    
    private final TextField nameInput = new TextField();
    private final TextField emailInput = new TextField();
    private final PasswordField passInput = new PasswordField();
    private final Label nameReq = new Label();
    private final Label emailReq = new Label();
    private final Label passReq = new Label();

    public RegisterView() {
        this.controller = new RegisterController();
        
        setPrefHeight(560.0);
        setPrefWidth(750.0);

        ImageView background = new ImageView(); 
        try {
            String bgPath = getClass().getResource("/images/bg.jpg").toExternalForm();
            background.setImage(new Image(bgPath));
        } catch (Exception e) { background.setStyle("-fx-background-color: #E0E0E0;"); }
        background.setFitHeight(560.0);
        background.setFitWidth(370.0);
        
        VBox backgroundOverlay = new VBox();
        backgroundOverlay.setPrefHeight(560.0);
        backgroundOverlay.setPrefWidth(370.0);
        backgroundOverlay.setStyle("-fx-background-color: #00a4bf80;");
        
        AnchorPane overlay = new AnchorPane();
        overlay.setPrefWidth(380.0);
        overlay.setPrefHeight(560.0);
        AnchorPane.setLeftAnchor(overlay, 370.0);
        AnchorPane.setTopAnchor(overlay, 0.0);

                // --- Logo ---
        ImageView logo = new ImageView();
        try {
            String logoPath = getClass().getResource("/images/logo.png").toExternalForm();
            logo.setImage(new Image(logoPath));
        } catch (Exception e) { /* ... */ }
        logo.setFitHeight(100.0); // Slightly smaller to fit new button
        logo.setFitWidth(100.0);
        logo.setPreserveRatio(true);
        AnchorPane.setTopAnchor(logo, 14.0);
        AnchorPane.setLeftAnchor(logo, 140.0); // Centered in overlay

        // --- Form ---
        Label titleLabel = new Label("Create Account");
        titleLabel.setTextFill(Color.web("#080c53"));
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20.0));
        AnchorPane.setTopAnchor(titleLabel, 60.0);
        AnchorPane.setLeftAnchor(titleLabel, 115.0);

        // Name
        Label nameLabel = new Label("Name*");
        AnchorPane.setTopAnchor(nameLabel, 100.0); AnchorPane.setLeftAnchor(nameLabel, 20.0);
        
        nameInput.setPrefHeight(35.0);
        nameInput.setPrefWidth(340.0);
        AnchorPane.setTopAnchor(nameInput, 120.0); 
        AnchorPane.setLeftAnchor(nameInput, 20.0);
        
        nameReq.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(nameReq, 155.0); AnchorPane.setLeftAnchor(nameReq, 20.0);

        // Email
        Label emailLabel = new Label("Email*");
        AnchorPane.setTopAnchor(emailLabel, 170.0); AnchorPane.setLeftAnchor(emailLabel, 20.0);
        
        emailInput.setPrefHeight(35.0); 
        emailInput.setPrefWidth(340.0);
        AnchorPane.setTopAnchor(emailInput, 190.0); 
        AnchorPane.setLeftAnchor(emailInput, 20.0);
        
        emailReq.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(emailReq, 225.0); 
        AnchorPane.setLeftAnchor(emailReq, 20.0);

        // Password
        Label passLabel = new Label("Password*");
        AnchorPane.setTopAnchor(passLabel, 240.0); AnchorPane.setLeftAnchor(passLabel, 20.0);
        
        passInput.setPrefHeight(35.0); 
        passInput.setPrefWidth(340.0);
        AnchorPane.setTopAnchor(passInput, 260.0);
        AnchorPane.setLeftAnchor(passInput, 20.0);
        
        passReq.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(passReq, 295.0); 
        AnchorPane.setLeftAnchor(passReq, 20.0);

        // Buttons
        Button createButton = new Button("Sign Up");
        createButton.setPrefHeight(40.0); createButton.setPrefWidth(340.0);
        createButton.setStyle("-fx-background-color: #00a4bf; -fx-text-fill: white; -fx-font-weight: bold;");
        createButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(createButton, 320.0); AnchorPane.setLeftAnchor(createButton, 20.0);

        // Google
        HBox divider = new HBox(10);
        divider.setAlignment(Pos.CENTER);
        divider.setPrefWidth(340);
        divider.getChildren().addAll(new Line(0,0,140,0), new Label("OR"), new Line(0,0,140,0));
        AnchorPane.setTopAnchor(divider, 375.0); AnchorPane.setLeftAnchor(divider, 20.0);

        Button googleButton = new Button("Sign up with Google");
        googleButton.setPrefHeight(40.0); googleButton.setPrefWidth(340.0);
        googleButton.setStyle("-fx-background-color: white; -fx-text-fill: #555; -fx-border-color: #ccc; -fx-font-weight: bold;");
        googleButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(googleButton, 405.0); AnchorPane.setLeftAnchor(googleButton, 20.0);

        Hyperlink loginLink = new Hyperlink("Already have an account? Login");
        AnchorPane.setTopAnchor(loginLink, 460.0); AnchorPane.setLeftAnchor(loginLink, 95.0);

        overlay.getChildren().addAll(
            logo, titleLabel,
            nameLabel, nameInput, nameReq,
            emailLabel, emailInput, emailReq,
            passLabel, passInput, passReq,
            createButton, divider, googleButton, loginLink
        );
        getChildren().addAll(background, backgroundOverlay, overlay);

        createButton.setOnAction(e -> handleRegisterAction());
        
        googleButton.setOnAction(e -> controller.handleGoogleSignup());
        loginLink.setOnAction(e -> controller.goLogin());
    }

    private void handleRegisterAction() {
        // 1. Clear Errors
        nameReq.setText("");
        emailReq.setText("");
        passReq.setText("");

        // 2. Prepare DTO
        RegisterRequest request = new RegisterRequest(
            nameInput.getText(),
            emailInput.getText(),
            passInput.getText()
        );

        // 3. Call Controller (Step 1: Initiate)
        ServiceResult result = controller.initiateRegister(request);

        // 4. Handle Result
        if (result.isSuccess()) {
            // SUCCESS: Show OTP Dialog (UI Responsibility!)
            showOtpDialog(request.email());
        } else {
            // ERROR: Show validations
            nameReq.setText(result.getFieldError("name"));
            emailReq.setText(result.getFieldError("email"));
            passReq.setText(result.getFieldError("password"));
        }
    }

    private void showOtpDialog(String email) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Email Verification");
        dialog.setHeaderText("We sent a code to " + email);
        dialog.setContentText("Enter the 4-digit code (Check console):");

        Optional<String> otpResult = dialog.showAndWait();
        if (otpResult.isPresent()) {
            // Call Controller (Step 2: Finalize)
            boolean finalSuccess = controller.finalizeRegister(email, otpResult.get());
            
            if (finalSuccess) {
                AlertUtils.infoBox("Account verified and created!", "Success");
                // Controller handles navigation to login
            } else {
                AlertUtils.errorBox("Invalid verification code.", "Error");
            }
        }
    }
}