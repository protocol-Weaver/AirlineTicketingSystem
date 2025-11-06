package artifact.Backend.Models;

public record DashboardStats(
    int airportCount,
    int aircraftCount,
    int crewCount,
    int reservationCount,
    int flightCount,
    int passengerCount
) {}
