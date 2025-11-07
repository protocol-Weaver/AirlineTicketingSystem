package artifact.Backend.Repositories.Interfaces;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Tags.BookingStatus;
import javafx.collections.ObservableList;

public interface ITicketRepository extends IRepository<Ticket> {
    ObservableList<Ticket> findByCustomerName(String customerName);
    void updateTicketStatus(long ticketId, BookingStatus newStatus);
}