package artifact.Backend.Repositories.Interfaces;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Tags.BookingStatus;
import java.util.List;

public interface IReservationRepository extends IRepository<Reservation> {
    Reservation addReservation(Reservation reservation); // Supabase needs to return the created item
    void updateReservationStatus(long reservationId, BookingStatus newStatus);
    /**
     * NEW: Finds all non-expired reservations for a specific flight.
     */
    List<Reservation> findByFlightId(long flightId);
}
