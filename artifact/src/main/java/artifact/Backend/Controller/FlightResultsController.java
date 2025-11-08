package artifact.Backend.Controller;

import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.View;
import java.util.List;

public class FlightResultsController extends BaseController {

    private final FlightSearchState flightSearchState;

    public FlightResultsController() {
        super();
        this.flightSearchState = FlightSearchState.getInstance();
    }

    /**
     * Get the data. The View will use this to build the UI.
     * We do NOT pass VBox here anymore.
     */
    public List<FlightSearchResult> getSearchResults() {
        return flightSearchState.getResults();
    }

    /**
     * Handle the booking action.
     */
    public void handleBookNow(FlightSearchResult selectedFlight) {
        flightSearchState.setSelectedFlight(selectedFlight);
        navigation.navigateTo(View.SEAT_SELECTION);
    }
    
    public void goUserBookingHome() { navigation.navigateTo(View.USER_BOOKING_HOME); }
    public void goMyBookings() { navigation.navigateTo(View.MY_BOOKINGS); }
}