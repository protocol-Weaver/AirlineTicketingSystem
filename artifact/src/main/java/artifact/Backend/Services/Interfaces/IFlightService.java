package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.Airport;
import artifact.Backend.Models.DTO.FlightRequest;
import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Models.ServiceResult;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

public interface IFlightService {
    /**
     * Admin: Adds a new flight.
     * Returns ServiceResult (Success/Fail) instead of manipulating UI labels.
     */
    ServiceResult addFlight(FlightRequest request);

    /**
     * User: Search for available dates.
     */
    Set<LocalDate> getAvailableFlightDates(Airport from, Airport to, YearMonth month);

    /**
     * User: Search for specific flights.
     */
    List<FlightSearchResult> searchFlights(Airport from, Airport to, LocalDate date);
}