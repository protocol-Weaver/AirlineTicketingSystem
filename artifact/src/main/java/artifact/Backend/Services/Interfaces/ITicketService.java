package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Models.User;
import javafx.collections.ObservableList;

public interface ITicketService {
    /**
     * Admin: Retrieves ALL tickets in the system.
     */
    ObservableList<Ticket> getAllTickets();

    /**
     * User: Retrieves tickets specific to a user.
     */
    ObservableList<Ticket> getMyTickets(User user);

    /**
     * User: Confirms payment for a ticket.
     */
    boolean confirmPayment(Ticket ticket, Reservation reservation);
}