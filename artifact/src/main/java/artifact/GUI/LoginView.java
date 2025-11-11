package artifact.GUI;
import artifact.Backend.AlertUtils;
import artifact.Backend.Controller.LoginController;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.DTO.LoginRequest;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * UPDATED: Added "Sign in with Google" button.
 */
public class LoginView extends AnchorPane {

    private final LoginController controller;
    
    private final TextField emailInput = new TextField();
    private final PasswordField passwordInput = new PasswordField();
    private final Label emailRequiredLabel = new Label();
    private final Label passwordRequiredLabel = new Label();
    
    public LoginView() {
        this.controller = new LoginController();
        
        setPrefHeight(560.0);
        setPrefWidth(750.0);

        // ... (Background & Logo setup code - keeping concise, assume same as before) ...
        // --- Background Image ---
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
        
        // --- Overlay Pane ---
        AnchorPane loginOverlay = new AnchorPane();
        loginOverlay.setPrefWidth(380.0);
        loginOverlay.setPrefHeight(560.0);
        loginOverlay.setStyle("-fx-background-color: #ffffff00;");
        AnchorPane.setLeftAnchor(loginOverlay, 370.0); 
        AnchorPane.setTopAnchor(loginOverlay, 0.0);

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
        Label welcomeLabel = new Label("Welcome Back");
        welcomeLabel.setTextFill(Color.web("#080c53"));
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 22.0));
        AnchorPane.setTopAnchor(welcomeLabel, 120.0);
        AnchorPane.setLeftAnchor(welcomeLabel, 110.0);

        // Email
        Label emailLabel = new Label("Email Address*");
        AnchorPane.setTopAnchor(emailLabel, 160.0);
        AnchorPane.setLeftAnchor(emailLabel, 20.0);
        
        emailInput.setPromptText("mail@example.com");
        emailInput.setPrefHeight(40.0);
        emailInput.setPrefWidth(340.0);
        AnchorPane.setTopAnchor(emailInput, 180.0);
        AnchorPane.setLeftAnchor(emailInput, 20.0);
        
        emailRequiredLabel.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(emailRequiredLabel, 222.0);
        AnchorPane.setLeftAnchor(emailRequiredLabel, 20.0);

        // Password
        Label passwordLabel = new Label("Password*");
        AnchorPane.setTopAnchor(passwordLabel, 240.0);
        AnchorPane.setLeftAnchor(passwordLabel, 20.0);
        
        passwordInput.setPromptText("*********");
        passwordInput.setPrefHeight(40.0);
        passwordInput.setPrefWidth(340.0);
        AnchorPane.setTopAnchor(passwordInput, 260.0);
        AnchorPane.setLeftAnchor(passwordInput, 20.0);
        
        passwordRequiredLabel.setTextFill(Color.RED);
        AnchorPane.setTopAnchor(passwordRequiredLabel, 302.0);
        AnchorPane.setLeftAnchor(passwordRequiredLabel, 20.0);

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setPrefHeight(40.0);
        loginButton.setPrefWidth(340.0);
        loginButton.setStyle("-fx-background-color: #00a4bf; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setCursor(Cursor.HAND);
        AnchorPane.setTopAnchor(loginButton, 330.0);
        AnchorPane.setLeftAnchor(loginButton, 20.0);

        // --- NEW: Divider ---
        HBox divider = new HBox(10);
        divider.setAlignment(Pos.CENTER);
        divider.setPrefWidth(340);
        Line line1 = new Line(0, 0, 140, 0); line1.setStroke(Color.LIGHTGRAY);
        Line line2 = new Line(0, 0, 140, 0); line2.setStroke(Color.LIGHTGRAY);
        Label orLabel = new Label("OR"); orLabel.setTextFill(Color.GRAY);
        divider.getChildren().addAll(line1, orLabel, line2);
        AnchorPane.setTopAnchor(divider, 385.0);
        AnchorPane.setLeftAnchor(divider, 20.0);

        // --- NEW: Google Button ---
        Button googleButton = new Button("Sign in with Google");
        googleButton.setPrefHeight(40.0);
        googleButton.setPrefWidth(340.0);
        googleButton.setStyle("-fx-background-color: white; -fx-text-fill: #555; -fx-border-color: #ccc; -fx-font-weight: bold;");
        googleButton.setCursor(Cursor.HAND);
        // Optional: Add G icon if you have it
        // ImageView gIcon = new ImageView(new Image("...")); gIcon.setFitHeight(20); googleButton.setGraphic(gIcon);
        
        AnchorPane.setTopAnchor(googleButton, 415.0);
        AnchorPane.setLeftAnchor(googleButton, 20.0);
        
        // Link
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
        AnchorPane.setTopAnchor(registerLink, 470.0);
        AnchorPane.setLeftAnchor(registerLink, 85.0);

        loginOverlay.getChildren().addAll(
            logo, welcomeLabel,
            emailLabel, emailInput, emailRequiredLabel,
            passwordLabel, passwordInput, passwordRequiredLabel,
            loginButton, 
            divider, googleButton, // Added
            registerLink
        );

        getChildren().addAll(background, backgroundOverlay, loginOverlay);

        // --- Bind Actions ---
        loginButton.setOnAction(e -> handleLoginAction());
        
        googleButton.setOnAction(e -> controller.handleGoogleLogin());
        registerLink.setOnAction(e -> controller.goRegister());
        passwordInput.setOnAction(e -> handleLoginAction());
    }


    private void handleLoginAction() {
        // 1. Clear Errors
        emailRequiredLabel.setText("");
        passwordRequiredLabel.setText("");

        // 2. Create DTO
        LoginRequest request = new LoginRequest(
            emailInput.getText(), 
            passwordInput.getText()
        );

        // 3. Call Controller
        ServiceResult result = controller.handleLogin(request);

        // 4. Handle Result (Only need to handle failure, success navigates away)
        if (!result.isSuccess()) {
            if (result.getGlobalError() != null) {
                AlertUtils.errorBox(result.getGlobalError(), "Login Failed");
            }
            emailRequiredLabel.setText(result.getFieldError("email"));
            passwordRequiredLabel.setText(result.getFieldError("password"));
        }
    }
}