package artifact.Backend.Controller;
import java.net.InetSocketAddress;
import java.net.URI;

import javafx.application.Platform;
import javafx.scene.control.Label;
import java.awt.Desktop;
import com.sun.net.httpserver.HttpServer;

import artifact.Backend.AlertUtils;
import artifact.Backend.UserSession;
import artifact.Backend.View;
import artifact.Backend.Models.GoogleUser;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.User;
import artifact.Backend.Models.DTO.LoginRequest;
import artifact.Backend.Services.Impl.AuthService;
import artifact.Backend.Services.Impl.GoogleOAuthService;
import artifact.Backend.Tags.UserRole;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IUserRepository;

import java.lang.Thread;

public class LoginController extends BaseController {

    private final AuthService authService;
    private final IUserRepository userRepo;

    public LoginController() {
        super();
        this.userRepo = RepositoryProvider.getUserRepository();
        this.authService = new AuthService(userRepo);
    }

    public ServiceResult handleLogin(LoginRequest request) {
        ServiceResult result = authService.login(request);

        if (result.isSuccess()) {
            User user = UserSession.getInstance().getCurrentUser();
            navigateBasedOnRole(user);
        }
        
        return result;
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


    private void startLocalCallbackServer() {
        new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);

                server.createContext("/callback", exchange -> {
                    String query = exchange.getRequestURI().getQuery(); // code=xxxx
                    String code = extractCodeFromQuery(query);

                    String response = "<h2>You may now close this window.</h2>";
                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.close();

                    server.stop(0);

                    finishGoogleLogin(code);
                });

                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    
    public void handleGoogleLogin() {
        GoogleOAuthService oauth = new GoogleOAuthService();
        String url = oauth.getAuthUrl();

        // Start local listener BEFORE opening browser
        startLocalCallbackServer();   // explains below

        // Open default browser
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    


    public void finishGoogleLogin(String authCode) {
        // Run network operation on a background thread
        new Thread(() -> {
            GoogleOAuthService service = new GoogleOAuthService();
            try {
                // Exchange code for token and fetch user details (NETWORK CALL)
                GoogleUser googleUser = service.fetchUserFromAuthCode(authCode);
                
                // Switch back to UI thread (Platform.runLater)
                Platform.runLater(() -> {
                    // Login/Register the user in our local system
                    User loggedIn = authService.loginWithGoogle(
                            googleUser.email(),
                            googleUser.name()
                    );
                    AlertUtils.infoBox("Logged in as " + loggedIn.name(), "Success");
                    navigateBasedOnRole(loggedIn);
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    AlertUtils.errorBox("Google login failed:\n" + e.getMessage(), "Error")
                );
                e.printStackTrace();
            }
        }).start();
    }
    
    private void navigateBasedOnRole(User user) {
        if (user.role() == UserRole.ADMIN) {
            navigation.navigateTo(View.HOME);
        } else if (user.role() == UserRole.STAFF) {
            navigation.navigateTo(View.STAFF_DASHBOARD);
        } else {
            navigation.navigateTo(View.USER_BOOKING_HOME);
        }
    }
}