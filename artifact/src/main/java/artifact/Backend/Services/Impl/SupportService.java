package artifact.Backend.Services.Impl;

import artifact.Backend.Models.DTO.TicketCreateRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.SupportMessage;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Models.User;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.ISupportRepository;
import artifact.Backend.Services.Interfaces.ISupportService;
import artifact.Backend.Tags.TicketStatus;
import artifact.Backend.Tags.UserRole;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing the Help Desk / Support Ticket system.
 * Handles ticket creation, message exchange between users and staff, and status updates.
 */
public class SupportService implements ISupportService {
    
    private final ISupportRepository supportRepository = RepositoryProvider.getSupportRepository();
    
    // Default constructor
    public SupportService() {}

    /**
     * Creates a new support ticket initiated by a user.
     *
     * @param user    The user creating the ticket.
     * @param request DTO containing the ticket subject and initial description.
     * @return ServiceResult indicating success or validation errors.
     */
    @Override
    public ServiceResult createTicket(User user, TicketCreateRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Validation: Ensure mandatory fields are present
        if (request.subject() == null || request.subject().trim().isEmpty()) {
            result.addError("subject", "Subject is required.");
        }
        if (request.description() == null || request.description().trim().isEmpty()) {
            result.addError("description", "Description is required.");
        }

        if (!result.isSuccess()) return result;
        
        // 2. Business Logic
        // Create the initial message object from the description
        SupportMessage msg = new SupportMessage(
            user.name(), 
            user.role(), 
            request.description(), 
            LocalDateTime.now()
        );
        
        List<SupportMessage> messages = new ArrayList<>();
        messages.add(msg);

        // Assemble the Ticket object (ID 0 is placeholder for DB auto-increment)
        SupportTicket ticket = new SupportTicket(
            0, 
            user.id(), 
            user.name(), 
            request.subject(),
            TicketStatus.OPEN, 
            LocalDate.now(), 
            messages
        );
        
        supportRepository.add(ticket);
        
        return result;
    }

    /**
     * Appends a new message to an existing support ticket.
     * Triggers simulated notifications to the recipient (Staff or User).
     *
     * @param ticketId The ID of the ticket to reply to.
     * @param sender   The User sending the message.
     * @param text     The content of the message.
     */
    @Override
    public void sendMessage(long ticketId, User sender, String text) {
        if (text == null || text.trim().isEmpty()) return;
        
        SupportMessage msg = new SupportMessage(sender.name(), sender.role(), text, LocalDateTime.now());
        supportRepository.addMessage(ticketId, msg);
        
        // Notification Logic Simulation
        // In a real app, this would integrate with NotificationManager
        if (sender.role() == UserRole.STAFF) {
             System.out.println("NOTIFICATION: Email to User: Support replied to ticket #" + ticketId);
        } else {
             System.out.println("NOTIFICATION: Alert to Staff: New reply on ticket #" + ticketId);
        }
    }

    /**
     * Retrieves all tickets created by a specific user.
     * @param user The user whose tickets should be fetched.
     * @return ObservableList for UI binding.
     */
    @Override
    public ObservableList<SupportTicket> getMyTickets(User user) {
        return supportRepository.findByUserId(user.id());
    }

    /**
     * Retrieves all tickets in the system (Admin/Staff view).
     * @return ObservableList of all tickets.
     */
    public ObservableList<SupportTicket> getAllTickets() {
        return supportRepository.getAll();
    }

    /**
     * Finds a specific ticket by its unique ID.
     * @param id The ticket ID.
     * @return The SupportTicket object or null if not found.
     */
    public SupportTicket findById(long id) {
        return supportRepository.findById(id);
    }

    /**
     * Updates the status of a ticket (e.g., OPEN -> RESOLVED).
     * @param ticketId The ticket ID.
     * @param status   The new status to apply.
     */
    @Override
    public void updateTicketStatus(long ticketId, TicketStatus status) {
        supportRepository.updateStatus(ticketId, status);
    }

    /**
     * Convenience method to mark a ticket as RESOLVED.
     * @param ticketId The ticket ID.
     */
    @Override
    public void resolveTicket(long ticketId) {
        updateTicketStatus(ticketId, TicketStatus.RESOLVED);
    }
}