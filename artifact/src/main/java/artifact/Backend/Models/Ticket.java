package artifact.Backend.Models;
import artifact.Backend.Tags.BookingStatus;
import java.time.LocalDate;

public record Ticket(
    long id,
    long reservationId,
    String customerName,
    BookingStatus paymentStatus, // Use enum
    String flightInfo,
    LocalDate flightDate
) {}