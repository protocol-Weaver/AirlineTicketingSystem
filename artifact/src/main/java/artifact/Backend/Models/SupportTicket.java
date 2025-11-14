package artifact.Backend.Models;
import artifact.Backend.Tags.TicketStatus;
import java.time.LocalDate;
import java.util.List;


public record SupportTicket(
    long id,
    long userId,
    String userName,
    String subject,
    TicketStatus status,
    LocalDate createdAt,
    List<SupportMessage> messages // The conversation history
) {}
