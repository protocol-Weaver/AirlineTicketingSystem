package artifact.Backend.Controller;

import java.util.Collections;
import java.util.Set;

import artifact.Backend.UserSession;
import artifact.Backend.View;
import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Services.Impl.ReservationService;
import artifact.Backend.AlertUtils; // Assuming you have this

/**
 * UPDATED: Handles multi-seat logic.
 */
public class SeatSelectionController extends BaseController {

    private final FlightSearchState state;
    private final ReservationService reservationService; 

    public SeatSelectionController() {
        super();
        this.state = FlightSearchState.getInstance();
        this.reservationService = new ReservationService(
            RepositoryProvider.getReservationRepository(), 
            RepositoryProvider.getTicketRepository(), 
            RepositoryProvider.getFlightRepository(), 
            UserSession.getInstance()
        );
    }
    
    /**
     * Toggles a seat selection.
     * @return TRUE if seat is now selected, FALSE if deselected or rejected.
     */
    public boolean toggleSeatSelection(String seatId) {
        Set<String> currentSeats = state.getSelectedSeats();
        int maxGuests = state.getGuestCount();

        if (currentSeats.contains(seatId)) {
            // Deselect
            state.removeSeat(seatId);
            System.out.println("Seat " + seatId + " removed.");
            return false;
        } else {
            // Select - Check Limit
            if (currentSeats.size() < maxGuests) {
                state.addSeat(seatId);
                System.out.println("Seat " + seatId + " added.");
                return true;
            } else {
                AlertUtils.errorBox("You have already selected " + maxGuests + " seats for your " + maxGuests + " guests.", "Seat Limit Reached");
                return false;
            }
        }
    }
    
    public boolean isSelectionComplete() {
        return state.isSelectionComplete();
    }

    /**
     * Called by the "Continue to Payment" button.
     */
    public void handleContinue() {
        if (!state.isSelectionComplete()) {
            int remaining = state.getGuestCount() - state.getSelectedSeats().size();
            AlertUtils.errorBox("Please select " + remaining + " more seat(s).", "Incomplete Selection");
            return;
        }
        navigation.navigateTo(View.PAYMENT);
    }

    public Set<String> getTakenSeatsForCurrentFlight() {
        FlightSearchResult flight = state.getSelectedFlight();
        if (flight == null) {
            return Collections.emptySet();
        }
        return reservationService.getTakenSeats(flight.flight().id());
    }
    
    // --- Navbar Navigation ---
    public void goUserBookingHome() { navigation.navigateTo(View.USER_BOOKING_HOME); }
    public void goMyBookings() { navigation.navigateTo(View.MY_BOOKINGS); }
}