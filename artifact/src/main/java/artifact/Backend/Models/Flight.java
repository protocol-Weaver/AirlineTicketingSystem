package artifact.Backend.Models;
import java.time.LocalDateTime;

public record Flight(
    long id,
    long departureAirportId,
    long arrivalAirportId,
    long aircraftId,
    long crewId,
    LocalDateTime departureTime,
    LocalDateTime arrivalTime,
    int availableSeats
) {}
