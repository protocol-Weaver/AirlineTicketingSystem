package artifact.Backend;

/**
 * Enum to define all available views (scenes) in the application.
 * This replaces string-based FXML paths, providing type-safety.
 */
public enum View {
    // Shared
    LOGIN,
    REGISTER,
    PAYMENT,

    // Admin Flow
    HOME,
    AIRPORTS,
    AIRCRAFTS,
    CREWS,
    FLIGHTS,
    RESERVATIONS,
    TICKETS,

    // Staff Flow
    STAFF_DASHBOARD,

    // Customer Flow
    USER_BOOKING_HOME,
    FLIGHT_RESULTS,
    SEAT_SELECTION, 
    MY_BOOKINGS,
    USER_SUPPORT
}