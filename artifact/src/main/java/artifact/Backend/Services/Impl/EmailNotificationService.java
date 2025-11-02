package artifact.Backend.Services.Impl;
import artifact.Backend.Models.Notification;
import io.github.cdimascio.dotenv.Dotenv;
import artifact.Backend.Services.Interfaces.INotificationObserver;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Observer implementation that sends emails via SMTP (Gmail).
 * Subscribes to the NotificationManager.
 */
public class EmailNotificationService implements INotificationObserver {

    // Note: Credentials are typically stored in environment variables for security.
    private final String smtpUser = "ProtocolCyberia@gmail.com";
    private final String smtpPassword;
    private final Session session;

    /**
     * Configures JavaMail properties and authenticates the session.
     */
    public EmailNotificationService() {
        Dotenv dotenv = Dotenv.load();
        smtpPassword = dotenv.get("SMTP_PASSWORD");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
    }

    /**
     * Triggered when the NotificationSubject notifies observers.
     * Sends the email asynchronously to prevent UI blocking.
     *
     * @param notification The notification data (Recipient, Subject, Body).
     */
    @Override
    public void onNotify(Notification notification) {
        // Run in a separate thread so the UI doesn't freeze while contacting the SMTP server
        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(smtpUser));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(notification.getRecipientEmail())
                );
                message.setSubject(notification.getSubject());
                message.setText(notification.getMessage());

                Transport.send(message);

                System.out.println(">> Email sent to: " + notification.getRecipientEmail());

            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}