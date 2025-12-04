package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Flight;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;

import com.google.gson.reflect.TypeToken;
import java.time.LocalDateTime;
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
        // FIXED: Using LocalDateTime and adding hours for duration
        dataList.add(new Flight(1, 1, 2, 1, 1, LocalDateTime.now().plusDays(10).withHour(10).withMinute(0), LocalDateTime.now().plusDays(10).plusHours(2), 416));
        dataList.add(new Flight(2, 2, 3, 2, 2, LocalDateTime.now().plusDays(12).withHour(14).withMinute(30), LocalDateTime.now().plusDays(12).plusHours(4), 180));
        dataList.add(new Flight(3, 3, 4, 2, 1, LocalDateTime.now().plusDays(5).withHour(07).withMinute(15), LocalDateTime.now().plusDays(5).plusHours(1), 180));
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
                // FIXED: YearMonth.from works with LocalDateTime
                .filter(f -> YearMonth.from(f.departureTime()).equals(month))
                .filter(f -> f.availableSeats() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        super.delete(id); 
        RepositoryProvider.getReservationRepository().refreshFromCloud();
        RepositoryProvider.getTicketRepository().refreshFromCloud();
        System.out.println("Cascading delete handled by Cloud. Local files updated.");
    }
}