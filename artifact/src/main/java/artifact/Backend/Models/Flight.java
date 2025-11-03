package artifact.Backend.Models;
import java.time.LocalDate;

public record Flight(
    long id,
    long departureAirportId,
    long arrivalAirportId,
    long aircraftId,
    long crewId,
    LocalDate departureTime,
    LocalDate arrivalTime,
    int availableSeats
) {}
