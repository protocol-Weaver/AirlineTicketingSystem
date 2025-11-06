package artifact.Backend.Controller;
import java.util.Collections;
import java.util.Set;

import artifact.Backend.UserSession;
import artifact.Backend.View;
import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Services.Impl.ReservationService;

/**
 * NEW: Controller for the SeatSelectionView.
 */
public class SeatSelectionController extends BaseController {

    private final FlightSearchState state;
    private final ReservationService reservationService; 


    public SeatSelectionController() {
        super();
        this.state = FlightSearchState.getInstance();
        this.reservationService = new ReservationService(RepositoryProvider.getReservationRepository(), RepositoryProvider.getTicketRepository(), RepositoryProvider.getFlightRepository(), UserSession.getInstance());
    }
    
    /**
     * Called by the View when a seat ToggleButton is clicked.
     * @param seatId The ID of the seat (e.g., "12A")
     */
    public void handleSeatSelected(String seatId) {
        state.setSelectedSeat(seatId);
        System.out.println("Seat " + seatId + " selected.");
    }
    
    /**
     * Called by the "Continue to Payment" button.
     */
    public void handleContinue() {
        if (state.getSelectedSeat() == null) {
            // This should be unreachable due to button disable logic
            return;
        }
        
        // All info is gathered, proceed to payment
        navigation.navigateTo(View.PAYMENT);
    }

    public Set<String> getTakenSeatsForCurrentFlight() {
        FlightSearchResult flight = state.getSelectedFlight();
        if (flight == null) {
            return Collections.emptySet(); // Safety check
        }
        // This call will now succeed
        return reservationService.getTakenSeats(flight.flight().id());
    }
    
    
    // --- Navbar Navigation ---
    public void goUserBookingHome() {
        navigation.navigateTo(View.USER_BOOKING_HOME);
    }
    
    public void goMyBookings() {
        navigation.navigateTo(View.MY_BOOKINGS);
    }
}