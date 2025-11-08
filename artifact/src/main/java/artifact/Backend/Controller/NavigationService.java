package artifact.Backend.Controller;
import artifact.Backend.View;
import artifact.GUI.AircraftsView;
import artifact.GUI.AirportsView;
import artifact.GUI.CrewsView;
import artifact.GUI.FlightResultsView;
import artifact.GUI.FlightsView;
import artifact.GUI.HomeView;
import artifact.GUI.LoginView;
import artifact.GUI.MyBookingsView;
import artifact.GUI.PaymentView;
import artifact.GUI.RegisterView;
import artifact.GUI.ReservationsView;
import artifact.GUI.SeatSelectionView;
import artifact.GUI.StaffDashboardView;
import artifact.GUI.TicketsView;
import artifact.GUI.UserBookingHomeView;
import artifact.GUI.UserSupportView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationService {

    private static NavigationService instance;
    private Stage primaryStage;

    private NavigationService() {}

    public static NavigationService getInstance() {
        if (instance == null) { instance = new NavigationService(); }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public void navigateTo(View view) {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set in NavigationService.");
        }
        Scene scene = createScene(view);
        String title = getTitleForView(view);
        primaryStage.setTitle("Airline Management System | " + title);
        primaryStage.setScene(scene);
    }

    private Scene createScene(View view) {
        final double ADMIN_WIDTH = 750;
        final double ADMIN_HEIGHT = 560;
        final double CUSTOMER_WIDTH = 900;
        final double CUSTOMER_HEIGHT = 650;

        switch (view) {
            // ... (Login, Register, Admin views are the same) ...
            case LOGIN: return new Scene(new LoginView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case REGISTER: return new Scene(new RegisterView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case HOME: return new Scene(new HomeView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case AIRPORTS: return new Scene(new AirportsView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case AIRCRAFTS: return new Scene(new AircraftsView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case CREWS: return new Scene(new CrewsView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case FLIGHTS: return new Scene(new FlightsView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case RESERVATIONS: return new Scene(new ReservationsView(), ADMIN_WIDTH, ADMIN_HEIGHT);
            case TICKETS: return new Scene(new TicketsView(), ADMIN_WIDTH, ADMIN_HEIGHT);

            // Customer Flow
            case USER_BOOKING_HOME:
                return new Scene(new UserBookingHomeView(), CUSTOMER_WIDTH, CUSTOMER_HEIGHT);
            case FLIGHT_RESULTS:
                return new Scene(new FlightResultsView(), CUSTOMER_WIDTH, CUSTOMER_HEIGHT);
            
            // --- NEW ---
            case SEAT_SELECTION:
                return new Scene(new SeatSelectionView(), CUSTOMER_WIDTH, CUSTOMER_HEIGHT + 100); // Taller for seat map
            // --- END NEW ---

            case PAYMENT:
                return new Scene(new PaymentView(), 900, 700);
            case MY_BOOKINGS:
                return new Scene(new MyBookingsView(), CUSTOMER_WIDTH, CUSTOMER_HEIGHT);

            case STAFF_DASHBOARD: return new Scene(new StaffDashboardView(), ADMIN_WIDTH, ADMIN_HEIGHT);

            case USER_SUPPORT:
                return new Scene(new UserSupportView(), CUSTOMER_WIDTH, CUSTOMER_HEIGHT);
                
            default:
                throw new IllegalArgumentException("Unknown view: " + view);
        }
    }

    private String getTitleForView(View view) {
        switch (view) {
            // ... (other titles) ...
            case USER_BOOKING_HOME: return "Book a Flight";
            case FLIGHT_RESULTS: return "Available Flights";
            case SEAT_SELECTION: return "Select Your Seat"; // <-- NEW
            case PAYMENT: return "Complete Payment";
            case MY_BOOKINGS: return "My Bookings";
            case STAFF_DASHBOARD: return "Staff Dashboard";
            default: return "Airline";
        }
    }
}