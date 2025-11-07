package artifact.Backend.Models;

public record FlightSearchResult(
    Flight flight,
    Airport departureAirport,
    Airport arrivalAirport,
    Aircraft aircraft
) {}