package artifact.Backend.Controller;

import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.util.Set;

import artifact.Backend.AlertUtils;
import artifact.Backend.View;
import artifact.Backend.Tags.BookingStatus;
import artifact.Backend.Models.FlightSearchResult;
import artifact.Backend.Models.Models;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.User;
import artifact.Backend.Models.DTO.BookingRequest;
import artifact.Backend.Notification.NotificationManager;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Repositories.Interfaces.ITicketRepository;
import artifact.Backend.Services.Impl.BookingService;
import artifact.GUI.StripePaymentWindow; 

public class PaymentController extends BaseController {

    private final BookingService bookingService;
    private final FlightSearchState state;

    private FlightSearchResult selectedFlight;
    private Set<String> selectedSeats; // UPDATED to Set
    private String selectedCabin;
    private int guestCount;
    private User currentUser;
    private double calculatedTotalPrice = 0.0;
    
    // Per-person price for individual records
    private double pricePerSeat = 0.0;

    private final IFlightRepository flightRepository;
    private final IReservationRepository reservationRepository;
    private final ITicketRepository ticketRepository;
    private final NotificationManager notificationManager;

    public PaymentController() {
        super();
        this.flightRepository = RepositoryProvider.getFlightRepository();
        this.notificationManager = NotificationManager.getInstance();
        this.ticketRepository = RepositoryProvider.getTicketRepository();
        this.reservationRepository = RepositoryProvider.getReservationRepository();
        this.bookingService = new BookingService(flightRepository, reservationRepository, ticketRepository, notificationManager);
        this.state = FlightSearchState.getInstance();

        this.selectedFlight = state.getSelectedFlight();
        this.selectedSeats = state.getSelectedSeats(); // UPDATED
        this.selectedCabin = state.getSelectedCabin();
        this.guestCount = state.getGuestCount();
        this.currentUser = userSession.getCurrentUser();
    }

    public void initialize(
            Label flightHeader, Label flightRoute, Label flightDate, Label flightTime,
            Label flightPassenger, Label baseFare, Label taxes, Label totalPrice
    ) {
        if (selectedFlight == null || selectedSeats.isEmpty()) {
            AlertUtils.errorBox("Booking session expired. Please restart your search.", "Error");
            navigation.navigateTo(View.USER_BOOKING_HOME);
            return;
        }

        flightHeader.setText("Flight to " + selectedFlight.arrivalAirport().location());
        flightRoute.setText(String.format("%s ➔ %s",
                selectedFlight.departureAirport().name(),
                selectedFlight.arrivalAirport().name()
        ));
        flightDate.setText(selectedFlight.flight().departureTime().format(Models.DATE_FORMATTER));
        flightTime.setText("10:00 AM - 02:30 PM (4h 30m)");
        
        // Show all seats (e.g. "12A, 12B")
        String seatString = String.join(", ", selectedSeats);
        flightPassenger.setText(String.format("%d %s, %s, Seats: %s",
                guestCount, (guestCount > 1 ? "Guests" : "Guest"), selectedCabin, seatString
        ));

        double mockBaseFare = 350.00;
        double mockTaxes = 45.50;
        double cabinMultiplier = selectedCabin.equals("Business") ? 2.5 :
                                 selectedCabin.equals("First Class") ? 4.0 : 1.0;

        this.calculatedTotalPrice = (mockBaseFare + mockTaxes) * cabinMultiplier * guestCount * 278;
        
        this.pricePerSeat = this.calculatedTotalPrice / guestCount;

        baseFare.setText(String.format("PKR %,.2f", (mockBaseFare * cabinMultiplier * guestCount * 278)));
        taxes.setText(String.format("PKR %,.2f", (mockTaxes * cabinMultiplier * guestCount * 278)));
        totalPrice.setText(String.format("PKR %,.2f", this.calculatedTotalPrice));
    }

    public void handlePayNow() {
        try {
            // Pass the TOTAL amount to Stripe
            StripePaymentWindow stripe = new StripePaymentWindow();
            stripe.open(this.calculatedTotalPrice, (transactionId) -> {
                System.out.println("Stripe Payment Successful. ID: " + transactionId);
                AlertUtils.infoBox("Payment successful via Stripe.", "Confirmed");
                performBooking(BookingStatus.CONFIRMED);
            });

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.errorBox("Stripe payment failed: " + e.getMessage(), "Payment Error");
        }
    }

    public void handlePayLater() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Pay Later");
        confirm.setHeaderText("Confirm Your Booking Reservation");
        confirm.setContentText("Your seats will be held, but you must pay at least 24 hours before departure.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            performBooking(BookingStatus.PENDING);
        }
    }

    // ==============================================================
    //     UPDATED: Loop through seats
    // ==============================================================
    private void performBooking(BookingStatus status) {
        boolean allSuccess = true;
        
        // Iterate through the Set of seats and create a request for EACH one.
        for (String seat : selectedSeats) {
            BookingRequest request = new BookingRequest(
                    selectedFlight,
                    currentUser,
                    seat,           // Current seat in loop
                    pricePerSeat,   // Split price
                    status
            ); 
            
            ServiceResult result = bookingService.bookFlight(request);
            if (!result.isSuccess()) {
                allSuccess = false;
                AlertUtils.errorBox("Failed to book seat " + seat + ": " + result.getGlobalError(), "Partial Booking Error");
            }
        }

        if (allSuccess) {
            state.clearState();
            navigation.navigateTo(View.MY_BOOKINGS);
        }
    }
}