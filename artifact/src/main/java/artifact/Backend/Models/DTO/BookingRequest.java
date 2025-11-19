package artifact.Backend.Models.DTO;

import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Models.User;
import artifact.Backend.Tags.BookingStatus;

public record BookingRequest(
    FlightSearchResult flightResult,
    User customer,
    String seatNumber,
    double price,
    BookingStatus status
) {}