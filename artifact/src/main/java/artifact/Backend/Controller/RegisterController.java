package artifact.Backend.Controller;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Optional;
import java.awt.Desktop;
import com.sun.net.httpserver.HttpServer;

import artifact.Backend.AlertUtils;
import artifact.Backend.Models.GoogleUser;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.User;
import artifact.Backend.Models.DTO.RegisterRequest;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IUserRepository;
import artifact.Backend.Services.Impl.AuthService;
import artifact.Backend.Services.Impl.GoogleOAuthService;

import java.lang.Thread;

public class RegisterController extends BaseController {

    private final AuthService authService;
    private final IUserRepository userRepo; 
    public RegisterController() {
        super();
        this.userRepo = RepositoryProvider.getUserRepository();
        this.authService = new AuthService(userRepo);
    }

    public ServiceResult initiateRegister(RegisterRequest request) {
        return authService.initiateRegistration(request);
   
    }

    public boolean finalizeRegister(String email, String otp) {
        boolean success = authService.verifyAndRegister(email, otp);
        if (success) {
            // Navigation is handled here because it's a Logic state change
            goLogin();
        }
        return success;
    }
    
    
    public void handleGoogleSignup() {
        GoogleOAuthService oauth = new GoogleOAuthService();
        String url = oauth.getAuthUrl();

        // Start the local server BEFORE opening the browser
        startLocalCallbackServerForSignup();

        try {
            // Open the default browser to Google OAuth page
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.errorBox("Failed to open browser for Google signup.", "Error");
        }
    }

    // Local callback server to catch redirect from Google
    private void startLocalCallbackServerForSignup() {
        new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);

                server.createContext("/callback", exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String code = extractCodeFromQuery(query);

                    String response = "<h2>You may now close this window.</h2>";
                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.close();

                    server.stop(0);

                    if (code != null) {
                        finishGoogleSignup(code);
                    } else {
                        Platform.runLater(() ->
                            AlertUtils.errorBox("Google signup failed: No code returned.", "Error")
                        );
                    }
                });

                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String extractCodeFromQuery(String query) {
        if (query == null) return null;
        for (String part : query.split("&")) {
            if (part.startsWith("code=")) {
                return part.substring("code=".length());
            }
        }
        return null;
    }

    public void finishGoogleSignup(String authCode) {
        new Thread(() -> {
            GoogleOAuthService service = new GoogleOAuthService();
            try {
                GoogleUser googleUser = service.fetchUserFromAuthCode(authCode);

                Platform.runLater(() -> {
                    User loggedIn = authService.loginWithGoogle(
                            googleUser.email(),
                            googleUser.name()
                    );
                    AlertUtils.infoBox("Google registration successful! Logging you in.", "Success");
                    goUserBookingHome();
                });
            } catch (Exception e) {
                Platform.runLater(() ->
                    AlertUtils.errorBox("Google registration failed:\n" + e.getMessage(), "Error")
                );
                e.printStackTrace();
            }
        }).start();
    }

}