package artifact.Backend.Notification;
import artifact.Backend.Models.Notification;
import artifact.Backend.Tags.NotificationType;

import artifact.Backend.Models.User;

public class NotificationFactory {

    public static Notification createRegistration(User user) {
        String subject = "Welcome to Skyline Travel!";
        String body = "Dear " + user.name() + ",\n\n" +
                      "Thank you for creating an account with us. We are ready to help you explore the world.\n" +
                      "Login now to book your first flight.";
        return new Notification(user.email(), subject, body, NotificationType.ACCOUNT_REGISTERED);
    }

    public static Notification createBookingConfirmed(String email, String name, long ticketId, String flightInfo) {
        String subject = "Booking Confirmed - Ticket #" + ticketId;
        String body = "Dear " + name + ",\n\n" +
                      "Your payment was successful. Your seat is confirmed for: " + flightInfo + ".\n" +
                      "Have a safe flight!";
        return new Notification(email, subject, body, NotificationType.BOOKING_PAID);
    }

    public static Notification createPaymentReminder(String email, String name, long reservationId) {
        String subject = "Action Required: Pay for Reservation #" + reservationId;
        String body = "Dear " + name + ",\n\n" +
                      "You have reserved a seat using 'Pay Later'.\n" +
                      "IMPORTANT: You have 24 hours to complete this payment, otherwise your booking will automatically expire.";
        return new Notification(email, subject, body, NotificationType.PAYMENT_REMINDER);
    }

    public static Notification createOtp(String email, String otp) {
        String subject = "Verify Your Email Address";
        String body = "Your verification code is: " + otp + "\n\n" +
                      "If you did not request this code, please ignore this email.";
        return new Notification(email, subject, body, NotificationType.OTP_SENT);
    }
}