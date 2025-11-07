package artifact.Backend.Controller;

import artifact.Backend.Models.DTO.ReservationRequest;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.Reservation;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;
import artifact.Backend.Repositories.Interfaces.IReservationRepository;
import artifact.Backend.Repositories.Interfaces.ITicketRepository;
import artifact.Backend.Services.Impl.ReservationService;
import artifact.Backend.UserSession;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ReservationsController extends BaseController {

    private final ReservationService reservationService;
    private final IReservationRepository reservationRepository;
    private final IFlightRepository flightRepository;
    private final ITicketRepository ticketRepo;

    public ReservationsController() {
        super();
        this.ticketRepo = RepositoryProvider.getTicketRepository();
        this.reservationRepository = RepositoryProvider.getReservationRepository();
        this.flightRepository = RepositoryProvider.getFlightRepository();
        
        // Initialize Service
        this.reservationService = new ReservationService(
            reservationRepository, 
            ticketRepo, 
            flightRepository, 
            UserSession.getInstance()
        );
    }

    // --- Initialization Logic ---
    public void initialize(TableView<Reservation> reservationsTable, ComboBox<Flight> flightComboBox) {
        
        // Setup Flight Dropdown
        flightComboBox.setItems(flightRepository.getAll());
        flightComboBox.setConverter(createFlightConverter());

        // Setup Table Columns
        TableColumn<Reservation, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<Reservation, String> adNameCol = new TableColumn<>("Admin Name");
        adNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().adminName()));

        TableColumn<Reservation, Long> flIdCol = new TableColumn<>("Flight ID");
        flIdCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().flightId()).asObject());

        TableColumn<Reservation, String> seatNo = new TableColumn<>("Seat Number");
        seatNo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().seatNumber()));

        TableColumn<Reservation, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().reservationDate()));

        TableColumn<Reservation, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().price()).asObject());
        
        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().toString()));

        reservationsTable.getColumns().setAll(idCol, adNameCol, flIdCol, seatNo, dateCol, priceCol, statusCol);
        reservationsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load Data
        reservationsTable.setItems(reservationRepository.getAll());
    }

    // --- Action Logic (Clean & SOLID) ---
    public ServiceResult addReservation(ReservationRequest request) {
        // We unpack the DTO here to pass to the service.
        // This keeps the Controller clean and the Service agnostic of the DTO if needed.
        return reservationService.addReservation(
            request.customerName(),
            request.customerPhone(),
            request.flight(),
            request.seatNumber(),
            request.reservationDate(),
            request.priceStr(),
            request.isPaid()
        );
    }

    // --- Helpers ---
    private StringConverter<Flight> createFlightConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Flight flight) {
                if (flight == null) return null;
                return "Flight " + flight.id() + " (Seats: " + flight.availableSeats() + ")";
            }
            @Override
            public Flight fromString(String string) { return null; }
        };
    }
}