package artifact.Backend.Models.DTO;

import artifact.Backend.Models.Airport;
import java.time.LocalDate;

public record FlightSearchRequest(
    Airport from,
    Airport to,
    LocalDate date,
    int guestCount,
    String cabinClass
) {}