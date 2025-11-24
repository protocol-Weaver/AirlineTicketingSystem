package artifact.Backend.Controller;

import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

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
import artifact.GUI.StripePaymentWindow; // Import the new Stripe Window

public class PaymentController extends BaseController {

    private final BookingService bookingService;
    private final FlightSearchState state;

    private FlightSearchResult selectedFlight;
    private String selectedSeat;
    private String selectedCabin;
    private int guestCount;
    private User currentUser;
    private double calculatedTotalPrice = 0.0;

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
        this.selectedSeat = state.getSelectedSeat();
        this.selectedCabin = state.getSelectedCabin();
        this.guestCount = state.getGuestCount();
        this.currentUser = userSession.getCurrentUser();
    }

    public void initialize(
            Label flightHeader, Label flightRoute, Label flightDate, Label flightTime,
            Label flightPassenger, Label baseFare, Label taxes, Label totalPrice
    ) {
        if (selectedFlight == null || selectedSeat == null) {
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
        flightPassenger.setText(String.format("%d %s, %s, Seat %s",
                guestCount, (guestCount > 1 ? "Guests" : "Guest"), selectedCabin, selectedSeat
        ));

        double mockBaseFare = 350.00;
        double mockTaxes = 45.50;
        double cabinMultiplier = selectedCabin.equals("Business") ? 2.5 :
                                 selectedCabin.equals("First Class") ? 4.0 : 1.0;

        this.calculatedTotalPrice =
                (mockBaseFare + mockTaxes) * cabinMultiplier * guestCount * 278;

        baseFare.setText(String.format("PKR %,.2f", (mockBaseFare * cabinMultiplier * guestCount * 278)));
        taxes.setText(String.format("PKR %,.2f", (mockTaxes * cabinMultiplier * guestCount * 278)));
        totalPrice.setText(String.format("PKR %,.2f", this.calculatedTotalPrice));
    }


    // ==============================================================
    //     UPDATED: Main Pay Now -> Uses Stripe
    // ==============================================================

    public void handlePayNow() {
        try {
            double amountRs = this.calculatedTotalPrice;

            // Use the new Stripe Window
            StripePaymentWindow stripe = new StripePaymentWindow();
            
            // The open method waits for interaction and runs the lambda on success
            stripe.open(amountRs, (transactionId) -> {
                System.out.println("Stripe Payment Successful. ID: " + transactionId);
                AlertUtils.infoBox("Payment successful via Stripe.", "Confirmed");
                performBooking(BookingStatus.CONFIRMED);
            });

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.errorBox("Stripe payment failed: " + e.getMessage(), "Payment Error");
        }
    }

    // ==============================================================
    //     Pay Later
    // ==============================================================

    public void handlePayLater() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Pay Later");
        confirm.setHeaderText("Confirm Your Booking Reservation");
        confirm.setContentText("Your seat will be held, but you must pay at least 24 hours before departure.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            performBooking(BookingStatus.PENDING);
        }
    }


    // ==============================================================
    //     FINAL BOOKING OPERATION
    // ==============================================================

    private void performBooking(BookingStatus status) {
        BookingRequest request = new BookingRequest(selectedFlight,
                currentUser,
                selectedSeat,
                calculatedTotalPrice,
                status); 
        ServiceResult success = bookingService.bookFlight(request);

        if (success.isSuccess()) {
            state.clearState();
            navigation.navigateTo(View.MY_BOOKINGS);
        }
    }
}