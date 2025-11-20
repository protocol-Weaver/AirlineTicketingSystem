// File: Backend/Models/DTOs/ReservationRequest.java
package artifact.Backend.Models.DTO;

import artifact.Backend.Models.Flight;
import java.time.LocalDate;

public record ReservationRequest(
    String customerName,
    String customerPhone,
    Flight flight,
    String seatNumber,
    LocalDate reservationDate,
    String priceStr,
    boolean isPaid
) {}