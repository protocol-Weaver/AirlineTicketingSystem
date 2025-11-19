package artifact.Backend.Repositories.Interfaces;
import artifact.Backend.Models.SupportMessage;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Tags.TicketStatus;
import javafx.collections.ObservableList;

public interface ISupportRepository extends IRepository<SupportTicket> {
    ObservableList<SupportTicket> findByUserId(long userId);
    
    /**
     * Adds a new message to the ticket's conversation.
     */
    void addMessage(long ticketId, SupportMessage message);
    void updateStatus(long ticketId, TicketStatus status);
}