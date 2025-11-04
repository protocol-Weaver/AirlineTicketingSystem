package artifact.Backend.Controller;

import artifact.Backend.UserSession;
import artifact.Backend.View;

/**
 * Abstract Base Controller for all other controllers to extend.
 *
 * Provides common dependencies (services) and navigation methods.
 * Child controllers no longer need to know *how* navigation works,
 * they just call `goHome()`, etc. (Liskov Substitution Principle)
 */
public abstract class BaseController {

    // Services are now protected final fields, available to all child controllers
    protected final NavigationService navigation;
    protected final UserSession userSession;

    public BaseController() {
        // Get the singleton instances
        this.navigation = NavigationService.getInstance();
        this.userSession = UserSession.getInstance();
    }

    // --- Common Navigation Methods ---
    // These methods are called by the Views (e.g., SidebarView)

    public void goHome() {
        navigation.navigateTo(View.HOME);
    }

    public UserSession getUserSession()
    {
        return userSession;
    }

    public void goLogin() {
        userSession.clearSession(); // Clear session data on logout
        navigation.navigateTo(View.LOGIN);
    }

    public void goRegister() {
        navigation.navigateTo(View.REGISTER);
    }

    public void goAirports() {
        navigation.navigateTo(View.AIRPORTS);
    }

    public void goAirCrafts() {
        navigation.navigateTo(View.AIRCRAFTS);
    }

    public void goFlights() {
        navigation.navigateTo(View.FLIGHTS);
    }

    public void goCrews() {
        navigation.navigateTo(View.CREWS);
    }

    public void goReservations() {
        navigation.navigateTo(View.RESERVATIONS);
    }

    public void goTickets() {
        navigation.navigateTo(View.TICKETS);
    }
    
    public void goPayment() {
        navigation.navigateTo(View.PAYMENT);
    }

    /**
     * Navigates to the customer's main flight booking page.
     * Called by UserNavbarView.
     */
    public void goUserBookingHome() {
        navigation.navigateTo(View.USER_BOOKING_HOME);
    }

    
    public void goMyBookings() {
        navigation.navigateTo(View.MY_BOOKINGS);
    }

    public void goUserSupport() { 
        navigation.navigateTo(View.USER_SUPPORT); 
    }

}
