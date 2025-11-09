package artifact.Backend.Services.Impl;

import artifact.Backend.Models.Notification;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Models.User;
import artifact.Backend.Notification.NotificationFactory;
import artifact.Backend.Notification.NotificationManager;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Repositories.Interfaces.ITicketRepository;
import artifact.Backend.Services.Interfaces.ITicketService;
import artifact.Backend.Tags.BookingStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Service implementation for managing Flight Tickets.
 * Handles fetching tickets for users/admins and processing payment confirmations.
 */
public class TicketService implements ITicketService {

    private final ITicketRepository ticketRepository;
    private final IReservationRepository reservationRepository;
    private final NotificationManager notificationManager;

    /**
     * Constructor with dependency injection.
     * Used primarily by the Admin Controller.
     *
     * @param ticketRepo      Repository for ticket data.
     * @param reservationRepo Repository for reservation data.
     */
    public TicketService(ITicketRepository ticketRepo, IReservationRepository reservationRepo) {
        this.ticketRepository = ticketRepo;
        this.reservationRepository = reservationRepo;
        this.notificationManager = NotificationManager.getInstance();
    }
    
    /**
     * Default constructor for User-side usage.
     * Resolves dependencies via the RepositoryProvider.
     */
    public TicketService() {
        // Ideally use a ServiceProvider, but for compatibility with existing code:
        this.ticketRepository = artifact.Backend.Repositories.Impl.RepositoryProvider.getTicketRepository();
        this.reservationRepository = artifact.Backend.Repositories.Impl.RepositoryProvider.getReservationRepository();
        this.notificationManager = NotificationManager.getInstance();
    }

    /**
     * Retrieves all tickets in the system.
     * @return ObservableList of all tickets (for Admin view).
     */
    @Override
    public ObservableList<Ticket> getAllTickets() {
        return ticketRepository.getAll();
    }

    /**
     * Retrieves tickets belonging to a specific customer.
     *
     * @param user The logged-in user.
     * @return ObservableList of tickets matching the user's name.
     */
    @Override
    public ObservableList<Ticket> getMyTickets(User user) {
        if (user == null) {
            return FXCollections.observableArrayList();
        }
        return ticketRepository.findByCustomerName(user.name());
    }
    
    /**
     * Confirms payment for a pending ticket.
     * <ol>
     * <li>Updates the Reservation status to CONFIRMED.</li>
     * <li>Updates the Ticket status to CONFIRMED.</li>
     * <li>Sends a confirmation email to the user.</li>
     * </ol>
     *
     * @param ticket      The ticket being paid for.
     * @param reservation The linked reservation object.
     * @return true if successful, false if an exception occurred.
     */
    @Override
    public boolean confirmPayment(Ticket ticket, Reservation reservation) {
        try {
            // Update Database Statuses
            reservationRepository.updateReservationStatus(reservation.id(), BookingStatus.CONFIRMED);
            ticketRepository.updateTicketStatus(ticket.id(), BookingStatus.CONFIRMED);
            
            // Send Notification
            Notification message = NotificationFactory.createBookingConfirmed(
                reservation.customerName(), 
                ticket.customerName(), 
                ticket.id(), 
                ticket.flightInfo()
            );
            notificationManager.notifyAll(message);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error confirming payment: " + e.getMessage());
            return false;
        }
    }
}