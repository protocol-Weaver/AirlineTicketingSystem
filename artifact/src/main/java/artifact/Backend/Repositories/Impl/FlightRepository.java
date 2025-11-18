package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Flight;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;

import com.google.gson.reflect.TypeToken;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightRepository extends BaseJsonRepository<Flight> implements IFlightRepository {

    public FlightRepository() {
        super("flights.json", new TypeToken<ArrayList<Flight>>(){}.getType(), "/flights", Flight::id);
    }

    @Override
    protected void seedData() {
        System.out.println("Seeding Flights...");
        dataList.add(new Flight(1, 1, 2, 1, 1, LocalDate.now().plusDays(10), LocalDate.now().plusDays(10), 416));
        dataList.add(new Flight(2, 2, 3, 2, 2, LocalDate.now().plusDays(12), LocalDate.now().plusDays(12), 180));
        dataList.add(new Flight(3, 3, 4, 2, 1, LocalDate.now().plusDays(5), LocalDate.now().plusDays(5), 180));
        save();
    }

    @Override
    public void decrementSeat(long id) {
        Flight old = findById(id);
        if (old == null || old.availableSeats() <= 0) return;

        Flight updated = new Flight(
            old.id(), old.departureAirportId(), old.arrivalAirportId(),
            old.aircraftId(), old.crewId(), old.departureTime(),
            old.arrivalTime(), old.availableSeats() - 1
        );
        update(updated);
    }

    @Override
    public List<Flight> findFlightsByRouteAndMonth(long fromId, long toId, YearMonth month) {
        return dataList.stream()
                .filter(f -> f.departureAirportId() == fromId && f.arrivalAirportId() == toId)
                .filter(f -> YearMonth.from(f.departureTime()).equals(month))
                .filter(f -> f.availableSeats() > 0)
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
        RepositoryProvider.getReservationRepository().refreshFromCloud();
        RepositoryProvider.getTicketRepository().refreshFromCloud();
        
        System.out.println("Cascading delete handled by Cloud. Local files updated.");
    }
}