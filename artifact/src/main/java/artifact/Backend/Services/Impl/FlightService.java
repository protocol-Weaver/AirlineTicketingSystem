package artifact.Backend.Services.Impl;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Models.DTO.FlightRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Interfaces.IAircraftRepository;
import artifact.Backend.Repositories.Interfaces.IAirportRepository;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;
import artifact.Backend.Services.Interfaces.IFlightService;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlightService implements IFlightService {

    private final IFlightRepository flightRepository;
    private final IAircraftRepository aircraftRepository;
    private final IAirportRepository airportRepository;

    public FlightService(IFlightRepository flight, IAircraftRepository aircraft, IAirportRepository airport) {
        this.flightRepository = flight;
        this.aircraftRepository = aircraft;
        this.airportRepository = airport;
    }

    @Override
    public ServiceResult addFlight(FlightRequest request) {
        ServiceResult result = new ServiceResult();

        if (request.depAirport() == null) result.addError("depAirport", "Departure airport must be selected*");
        if (request.arrAirport() == null) result.addError("arrAirport", "Arrival airport must be selected*");
        if (request.aircraft() == null) result.addError("aircraft", "Aircraft must be selected*");
        if (request.crew() == null) result.addError("crew", "Crew must be selected*");
        if (request.depDate() == null) result.addError("depDate", "Departure date is required*");
        if (request.arrDate() == null) result.addError("arrDate", "Arrival date is required*");

        if (request.depAirport() != null && request.arrAirport() != null 
            && request.depAirport().id() == request.arrAirport().id()) {
            result.setGlobalError("Departure and Arrival airports cannot be the same.");
        }
        
        if (request.depDate() != null && request.arrDate() != null 
            && request.arrDate().isBefore(request.depDate())) {
            result.setGlobalError("Arrival date cannot be before departure date.");
        }

        if (!result.isSuccess()) return result;

        int capacity = aircraftRepository.findById(request.aircraft().id()).capacity();
        
        flightRepository.add(new Flight(
            0,
            request.depAirport().id(), 
            request.arrAirport().id(), 
            request.aircraft().id(), 
            request.crew().id(), 
            // FIXED: Convert Request LocalDate to LocalDateTime
            request.depDate().atStartOfDay(), 
            request.arrDate().atStartOfDay(), 
            capacity
        ));
        
        return result;
    }

    @Override
    public Set<LocalDate> getAvailableFlightDates(Airport from, Airport to, YearMonth month) {
        if (from == null || to == null) return Collections.emptySet();
        
        return flightRepository.findFlightsByRouteAndMonth(from.id(), to.id(), month).stream()
            // FIXED: Convert LocalDateTime back to LocalDate for the Calendar UI
            .map(f -> f.departureTime().toLocalDate())
            .collect(Collectors.toSet());
    }

    @Override
    public List<FlightSearchResult> searchFlights(Airport from, Airport to, LocalDate date) {
        List<Flight> matchingFlights = flightRepository.getAll().stream()
            .filter(f -> f.departureAirportId() == from.id())
            .filter(f -> f.arrivalAirportId() == to.id())
            // FIXED: Compare only the Date part of the LocalDateTime
            .filter(f -> f.departureTime().toLocalDate().isEqual(date))
            .filter(f -> f.availableSeats() > 0)
            .collect(Collectors.toList());
        
        return matchingFlights.stream()
            .map(flight -> new FlightSearchResult(
                flight,
                airportRepository.findById(flight.departureAirportId()),
                airportRepository.findById(flight.arrivalAirportId()),
                aircraftRepository.findById(flight.aircraftId())
            ))
            .collect(Collectors.toList());
    }
}