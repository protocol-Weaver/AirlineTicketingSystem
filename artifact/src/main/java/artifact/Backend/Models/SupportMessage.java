package artifact.Backend.Models;
import artifact.Backend.Tags.UserRole;
import java.time.LocalDateTime;

public record SupportMessage(
    String senderName,    // "John Admin" or "Sam Customer"
    UserRole senderRole,  // To style the chat bubbles (Left/Right)
    String message,
    LocalDateTime timestamp
) {}
