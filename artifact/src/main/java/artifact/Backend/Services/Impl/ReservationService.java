package artifact.Backend.Services.Impl;
import artifact.Backend.Tags.BookingStatus;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Repositories.Interfaces.ITicketRepository;
import artifact.Backend.Services.Interfaces.IReservationService;
import artifact.Backend.UserSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Service implementation for Admin-side Reservation Management.
 * Handles manual booking creation by admins and seat map retrieval.
 */
public class ReservationService implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final ITicketRepository ticketRepository;
    private final IFlightRepository flightRepository;
    private final UserSession userSession;

    public ReservationService(IReservationRepository reservationRepository,
                              ITicketRepository ticketRepository,
                              IFlightRepository flightRepository,
                              UserSession userSession) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.userSession = userSession;
    }

    /**
     * Manually adds a reservation (Admin functionality).
     * Validates input, checks flight capacity, and generates associated Ticket.
     *
     * @param custName  Customer Name.
     * @param custPhone Customer Phone.
     * @param flight    Selected Flight Object.
     * @param seatNum   Selected Seat Number.
     * @param resDate   Date of reservation.
     * @param priceStr  Price as String (parsed internally).
     * @param isPaid    Boolean flag to set status (Confirmed vs Pending).
     * @return ServiceResult indicating success or errors.
     */
    @Override
    public ServiceResult addReservation(String custName, String custPhone, Flight flight, String seatNum, LocalDate resDate, String priceStr, boolean isPaid) 
    {
        ServiceResult result = new ServiceResult();
        
        // 1. Validation Logic
        if (custName == null || custName.trim().isEmpty()) {
            result.addError("name", "Customer name is required*");
        }
        if (custPhone == null || custPhone.trim().isEmpty()) {
            result.addError("phone", "Customer phone is required*");
        }
        if (flight == null) {
            result.addError("flight", "Flight must be selected*");
        }
        
        // Validate Price Format
        double price = 0.0; 
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) result.addError("price", "Price must be positive*");
        } catch (NumberFormatException e) {
            result.addError("price", "Invalid price*");
        }

        // Check availability
        if (flight != null) {
            Flight flightData = flightRepository.findById(flight.id());
            if (flightData.availableSeats() <= 0) {
                result.setGlobalError("No available seats on this flight.");
                return result; 
            }
        }

        if (!result.isSuccess()) return result;

        // 2. Business Logic
        String adminName = userSession.getAdminName(); // Track which admin made the booking
        flightRepository.decrementSeat(flight.id());
        
        // Determine Status and Expiry logic
        BookingStatus status = isPaid ? BookingStatus.CONFIRMED : BookingStatus.PENDING;
        LocalDateTime expiry = isPaid ? null : LocalDateTime.now().plusDays(1);

        // Persist Reservation
        Reservation newReservation = new Reservation(0, flight.id(), custName, custPhone, seatNum, resDate, price, status, adminName, expiry);
        Reservation createdReservation = reservationRepository.addReservation(newReservation);
        
        // Persist Ticket
        Ticket newTicket = new Ticket(0, createdReservation.id(), custName, status, "Flight " + flight.id(), flight.departureTime());
        ticketRepository.add(newTicket);
        
        return result; 
    }
    
    /**
     * Retrieves a set of occupied seat numbers for a specific flight.
     * Used to render the visual seat map.
     */
    public Set<String> getTakenSeats(long flightId) 
    {
        List<Reservation> reservations = reservationRepository.findByFlightId(flightId);
        return reservations.stream()
            .map(Reservation::seatNumber)
            .collect(Collectors.toSet());
    }
}