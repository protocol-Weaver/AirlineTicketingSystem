package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.DTO.TicketCreateRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Models.User;
import artifact.Backend.Tags.TicketStatus;
import javafx.collections.ObservableList;

public interface ISupportService {

    /**
     * Creates a new support ticket.
     * Returns ServiceResult to decouple logic from UI.
     */
    ServiceResult createTicket(User user, TicketCreateRequest request);

    /**
     * Adds a message to an existing ticket.
     */
    void sendMessage(long ticketId, User sender, String text);

    /**
     * Retrieves tickets for a specific user.
     */
    ObservableList<SupportTicket> getMyTickets(User user);

    /**
     * Updates ticket status.
     */
    void updateTicketStatus(long ticketId, TicketStatus status);

    void resolveTicket(long ticketId);
}