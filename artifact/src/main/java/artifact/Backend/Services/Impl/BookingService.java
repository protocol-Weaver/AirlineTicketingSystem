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
    
    @Override
    public ServiceResult bookFlight(BookingRequest request) {
        ServiceResult result = new ServiceResult();

        if (request.flightResult() == null) {
            result.addError("flight", "Flight details missing.");
        }
        if (request.customer() == null) {
            result.addError("customer", "Customer details missing.");
        }
        if (!result.isSuccess()) return result;

        Flight flight = request.flightResult().flight();
        
        if (flight.availableSeats() <= 0) {
            result.setGlobalError("Sorry, this flight just sold out.");
            return result;
        }
        
        flightRepository.decrementSeat(flight.id());
        
        BookingStatus status = request.status();
        LocalDateTime expiry = (status == BookingStatus.PENDING) ? 
            flight.departureTime().minusDays(1) : null;

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
        
        Ticket newTicket = new Ticket(
            0, 
            createdReservation.id(), 
            request.customer().name(), 
            status, 
            flightInfo, 
            flight.departureTime().toLocalDate()
        );
        ticketRepository.add(newTicket);

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