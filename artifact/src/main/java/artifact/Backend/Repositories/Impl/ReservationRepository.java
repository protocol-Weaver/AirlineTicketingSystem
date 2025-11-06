package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Reservation;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Tags.BookingStatus;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationRepository extends BaseJsonRepository<Reservation> implements IReservationRepository {

    public ReservationRepository() {
        super("reservations.json", new TypeToken<ArrayList<Reservation>>(){}.getType(), "/reservations", Reservation::id);
    }

    @Override
    public Reservation addReservation(Reservation r) {
        long newId = generateNextId();
        Reservation newR = new Reservation(
            newId, r.flightId(), r.customerName(), r.customerPhone(),
            r.seatNumber(), r.reservationDate(), r.price(),
            r.status(), r.adminName(), r.expiryTime()
        );
        add(newR); 
        return newR;
    }

    @Override
    public void updateReservationStatus(long id, BookingStatus status) {
        Reservation old = findById(id);
        if (old == null) return;

        Reservation updated = new Reservation(
            old.id(), old.flightId(), old.customerName(), old.customerPhone(),
            old.seatNumber(), old.reservationDate(), old.price(),
            status, old.adminName(), null // Assuming expiry is cleared on status change
        );
        update(updated);
    }

    @Override
    public List<Reservation> findByFlightId(long flightId) {
        return dataList.stream()
                .filter(r -> r.flightId() == flightId)
                .filter(r -> r.status() == BookingStatus.PENDING || r.status() == BookingStatus.CONFIRMED)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        // 1. Delete the User (This triggers Supabase DELETE)
        // Supabase will automatically delete the user AND cascading tickets/reservations
        super.delete(id); 

        // 2. Wait a brief moment for Supabase to finish the Cascade? 
        // Usually it's instant, but safe to fetch immediately.

        // 3. Tell related repositories to refresh themselves
        RepositoryProvider.getTicketRepository().refreshFromCloud();
        
        System.out.println("Cascading delete handled by Cloud. Local files updated.");
    }
}