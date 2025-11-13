package artifact.Backend.Services.Impl;

import artifact.Backend.Models.DTO.BookingRequest;
import artifact.Backend.Notification.NotificationFactory;
import artifact.Backend.Notification.NotificationManager;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.Notification;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Repositories.Interfaces.ITicketRepository;
import artifact.Backend.Services.Interfaces.IBookingService;
import artifact.Backend.Tags.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service implementation for the Customer-side booking flow.
 * Coordinates Seat updates, Reservation creation, Ticket generation, and Notifications.
 */
public class BookingService implements IBookingService {

    private final IFlightRepository flightRepository;
    private final IReservationRepository reservationRepository;
    private final ITicketRepository ticketRepository;
    private final NotificationManager notificationManager;

    public BookingService(
        IFlightRepository flightRepository,
        IReservationRepository reservationRepository,
        ITicketRepository ticketRepository,
        NotificationManager notificationService) 
    {
        this.flightRepository = flightRepository;
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.notificationManager = notificationService;
    }
    
    /**
     * Processes a flight booking request from a customer.
     * * Steps:
     * 1. Validates inputs.
     * 2. Checks seat availability.
     * 3. Decrements seat count, creates Reservation, creates Ticket.
     * 4. Triggers email notifications via Observer pattern.
     *
     * @param request DTO containing flight search result, customer info, and booking status.
     * @return ServiceResult indicating success or failure (e.g., sold out).
     */
    @Override
    public ServiceResult bookFlight(BookingRequest request) {
        ServiceResult result = new ServiceResult();

        // 1. Validation
        if (request.flightResult() == null) {
            result.addError("flight", "Flight details missing.");
        }
        if (request.customer() == null) {
            result.addError("customer", "Customer details missing.");
        }
        if (!result.isSuccess()) return result;

        Flight flight = request.flightResult().flight();
        
        // 2. Business Logic Check: Concurrency safety check for seats
        if (flight.availableSeats() <= 0) {
            result.setGlobalError("Sorry, this flight just sold out.");
            return result;
        }
        
        // 3. Execution: Update Flight Capacity
        flightRepository.decrementSeat(flight.id());
        
        // Determine booking status and expiration time (Pending bookings expire in 24 hours relative to flight)
        BookingStatus status = request.status();
        LocalDateTime expiry = (status == BookingStatus.PENDING) ? 
            flight.departureTime().atStartOfDay().minusDays(1) : null;

        // Create and Persist Reservation
        Reservation newReservation = new Reservation(
            0, 
            flight.id(), 
            request.customer().name(), 
            "N/A (Customer)", 
            request.seatNumber(), 
            LocalDate.now(), 
            request.price(), 
            status, 
            "Customer Booking", 
            expiry
        );
        Reservation createdReservation = reservationRepository.addReservation(newReservation);
        
        String flightInfo = String.format("%s ➔ %s", 
            request.flightResult().departureAirport().name(), 
            request.flightResult().arrivalAirport().name()
        );
        
        // Create and Persist Ticket
        Ticket newTicket = new Ticket(
            0, 
            createdReservation.id(), 
            request.customer().name(), 
            status, 
            flightInfo, 
            flight.departureTime()
        );
        ticketRepository.add(newTicket);

        // 4. Notification Handling: Trigger email events based on status
        if(status == BookingStatus.CONFIRMED) {
            Notification message = NotificationFactory.createBookingConfirmed(
                request.customer().email(), 
                request.customer().name(), 
                newTicket.id(), 
                flightInfo
            );
            notificationManager.notifyAll(message);

        } else {
            Notification message = NotificationFactory.createPaymentReminder(
                request.customer().email(), 
                request.customer().name(), 
                createdReservation.id()
            );
            notificationManager.notifyAll(message);
        }
        
        return result;
    }
}