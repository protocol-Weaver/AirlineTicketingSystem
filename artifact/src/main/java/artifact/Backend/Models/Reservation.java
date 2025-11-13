package artifact.Backend.Models;
import artifact.Backend.Tags.BookingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Reservation(
    long id,
    long flightId,
    String customerName,
    String customerPhone,
    String seatNumber,
    LocalDate reservationDate,
    double price,
    BookingStatus status, // Replaces boolean isPaid
    String adminName,
    LocalDateTime expiryTime // For "Pay Later"
) {}