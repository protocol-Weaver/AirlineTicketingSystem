package artifact.Backend;

import artifact.Backend.Models.User;
import artifact.Backend.Tags.UserRole;

/**
 * Singleton service for managing the logged-in user's state.
 * This is unchanged from the previous refactor.
 */
public class UserSession {

    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void startSession(User user) {
        this.currentUser = user;
    }

    public void clearSession() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        if (!isSessionActive()) {
            // This prevents NullPointerExceptions if a view tries to get
            // user info before login. A better approach might be to
            // return an Optional or a "Guest" user object.
            return new User(0, "Guest", "guest@airline.com", "", UserRole.CUSTOMER); // Default to customer to prevent nulls
        }
        return currentUser;
    }

    public String getAdminName() {
        return getCurrentUser().name();
    }

    public String getAdminEmail() {
        return getCurrentUser().email();
    }

    public boolean isSessionActive() {
        return currentUser != null;
    }
}