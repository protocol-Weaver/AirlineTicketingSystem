package artifact.Backend.Controller;

import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.Ticket;
import artifact.Backend.Models.User;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Services.Impl.TicketService;
import artifact.Backend.View;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

public class MyBookingsController extends BaseController {

    private final TicketService ticketService;
    private final IReservationRepository reservationRepository;

    public MyBookingsController() {
        super();
        this.ticketService = new TicketService();
        this.reservationRepository = RepositoryProvider.getReservationRepository();
    }

    public void initialize(TableView<Ticket> ticketsTable, TableColumn<Ticket, Void> actionCol) {
        User currentUser = userSession.getCurrentUser();
        
        TableColumn<Ticket, Long> idCol = new TableColumn<>("Ticket ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<Ticket, String> flightCol = new TableColumn<>("Flight");
        flightCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().flightInfo()));
        flightCol.setPrefWidth(250.0);

        TableColumn<Ticket, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().flightDate()));
        dateCol.setPrefWidth(150.0);

        TableColumn<Ticket, String> statusCol = new TableColumn<>("Payment Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().paymentStatus().toString()));
        
        ticketsTable.getColumns().setAll(idCol, flightCol, dateCol, statusCol, actionCol);
        ticketsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ticketsTable.setItems(ticketService.getMyTickets(currentUser));
    }
    
    /**
     * Step 1 of Payment: Get the reservation logic.
     * Does NOT open the modal. Returns data for the View to use.
     */
    public Reservation getReservationForTicket(Ticket ticket) {
        return reservationRepository.findById(ticket.reservationId());
    }

    /**
     * Step 2 of Payment: Process the logic.
     * Returns Result. Does NOT show alerts.
     */
    public ServiceResult processPayment(Ticket ticket, Reservation reservation) {
        ServiceResult result = new ServiceResult();
        
        // Delegate to TicketService (assuming it returns boolean)
        boolean success = ticketService.confirmPayment(ticket, reservation);
        
        if (!success) {
            result.setGlobalError("An error occurred while confirming your payment.");
        }
        
        return result;
    }
    
    public void goUserBookingHome() { navigation.navigateTo(View.USER_BOOKING_HOME); }
    public void goMyBookings() { navigation.navigateTo(View.MY_BOOKINGS); }
}