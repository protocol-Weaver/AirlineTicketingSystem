package artifact.Backend.Controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import artifact.Backend.View;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.DTO.FlightSearchRequest;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IAirportRepository;
import artifact.Backend.Services.Impl.FlightService;

public class UserBookingHomeController extends BaseController {

    private final IAirportRepository airportRepository;
    private final FlightSearchState flightSearchState;
    private final FlightService flightService;

    public UserBookingHomeController() {
        super();
        this.airportRepository = RepositoryProvider.getAirportRepository();
        this.flightSearchState = FlightSearchState.getInstance();
        this.flightService = new FlightService(
            RepositoryProvider.getFlightRepository(), 
            RepositoryProvider.getAircraftRepository(), 
            RepositoryProvider.getAirportRepository()
        );
    }

    public List<Airport> getAllAirports() {
        return airportRepository.getAll();
    }

    public Set<LocalDate> getAvailableDatesForRoute(Airport from, Airport to, YearMonth month) {
        return flightService.getAvailableFlightDates(from, to, month);
    }

    /**
     * UPDATED: Uses DTO and Result pattern.
     * The Wizard (View) calls this and handles the result.
     */
    public ServiceResult performSearch(FlightSearchRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Validation
        if (request.from().id() == request.to().id()) {
            result.setGlobalError("'From' and 'To' airports cannot be the same.");
            return result;
        }

        // 2. Call Service
        List<FlightSearchResult> results = flightService.searchFlights(
            request.from(), 
            request.to(), 
            request.date()
        );
        
        // 3. Update State (Logic)
        flightSearchState.setResults(results);
        flightSearchState.setGuestCount(request.guestCount());
        flightSearchState.setSelectedCabin(request.cabinClass());
        
        // 4. Navigate (Only on success)
        navigation.navigateTo(View.FLIGHT_RESULTS);
        
        return result;
    }

    // --- Navigation ---
    public void goUserBookingHome() { navigation.navigateTo(View.USER_BOOKING_HOME); }
    public void goMyBookings() { navigation.navigateTo(View.MY_BOOKINGS); }
}