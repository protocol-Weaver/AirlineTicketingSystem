package artifact.Backend.Models;
import artifact.Backend.Tags.NotificationType;

public class Notification {
    private final String recipientEmail;
    private final String subject;
    private final String message;
    private final NotificationType type;

    public Notification(String recipientEmail, String subject, String message, NotificationType type) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        this.type = type;
    }

    public String getRecipientEmail() { return recipientEmail; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public NotificationType getType() { return type; }
}