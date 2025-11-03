package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.Ticket;
import artifact.Backend.Repositories.Interfaces.ITicketRepository;
import artifact.Backend.Tags.BookingStatus;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TicketRepository extends BaseJsonRepository<Ticket> implements ITicketRepository {

    public TicketRepository() {
        super("tickets.json", new TypeToken<ArrayList<Ticket>>(){}.getType(), "/tickets", Ticket::id);
    }

    @Override
    public void add(Ticket item) {
        // Override add to ensure ID is generated if the caller didn't set it
        // (Though usually, you'd let the Base handle generation, 
        // but your original code had explicit construction here).
        long newId = generateNextId();
        Ticket t = new Ticket(
            newId, item.reservationId(), item.customerName(),
            item.paymentStatus(), item.flightInfo(), item.flightDate()
        );
        super.add(t);
    }

    @Override
    public ObservableList<Ticket> findByCustomerName(String name) {
        return dataList.stream()
                .filter(t -> t.customerName().equalsIgnoreCase(name))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    @Override
    public void updateTicketStatus(long id, BookingStatus status) {
        Ticket old = findById(id);
        if (old == null) return;

        Ticket updated = new Ticket(
            old.id(), old.reservationId(), old.customerName(),
            status, old.flightInfo(), old.flightDate()
        );
        update(updated);
    }
}