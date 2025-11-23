package artifact.Backend.Controller;

import artifact.Backend.Models.Ticket;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Services.Impl.TicketService;
import artifact.Backend.Services.Interfaces.ITicketService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TicketsController extends BaseController {

    // Fix: Controller talks to Service, NOT Repository directly.
    private final ITicketService ticketService;

    public TicketsController() {
        super();
        // Inject dependencies into the service
        this.ticketService = new TicketService(
            RepositoryProvider.getTicketRepository(),
            RepositoryProvider.getReservationRepository()
        );
    }

    public void initialize(TableView<Ticket> ticketsTable) {
        
        TableColumn<Ticket, Long> idCol = new TableColumn<>("Ticket ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<Ticket, Long> resIdCol = new TableColumn<>("Reservation ID");
        resIdCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().reservationId()).asObject());

        TableColumn<Ticket, String> cName = new TableColumn<>("Customer Name");
        cName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().customerName()));

        TableColumn<Ticket, String> status = new TableColumn<>("Payment Status");
        status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().paymentStatus().toString()));
        
        // Add Flight Info Column (It's useful for admins)
        TableColumn<Ticket, String> flightCol = new TableColumn<>("Flight Info");
        flightCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().flightInfo()));

        ticketsTable.getColumns().setAll(idCol, resIdCol, cName, flightCol, status);
        ticketsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Fetch data via Service
        ticketsTable.setItems(ticketService.getAllTickets());
    }
}