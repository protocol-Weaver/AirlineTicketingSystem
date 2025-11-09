package artifact.Backend.Models.DTO;

import artifact.Backend.Models.Aircraft;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.Crew;
import java.time.LocalDate;

public record FlightRequest(
    Airport depAirport,
    Airport arrAirport,
    Aircraft aircraft,
    Crew crew,
    LocalDate depDate,
    LocalDate arrDate
) {}