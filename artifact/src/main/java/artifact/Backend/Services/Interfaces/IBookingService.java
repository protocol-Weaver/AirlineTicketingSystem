package artifact.Backend.Services.Interfaces;
import artifact.Backend.Models.DTO.BookingRequest;
import artifact.Backend.Models.ServiceResult;

public interface IBookingService {
    /**
     * Processes a flight booking.
     * Returns a ServiceResult instead of boolean/void.
     */
    ServiceResult bookFlight(BookingRequest request);
}