package artifact.Backend.Controller;

import artifact.Backend.Models.Aircraft;
import artifact.Backend.Models.Airport;
import artifact.Backend.Models.Crew;
import artifact.Backend.Models.Flight;
import artifact.Backend.Models.DTO.FlightRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IAircraftRepository;
import artifact.Backend.Repositories.Interfaces.IAirportRepository;
import artifact.Backend.Repositories.Interfaces.ICrewRepository;
import artifact.Backend.Repositories.Interfaces.IFlightRepository;
import artifact.Backend.Services.Impl.FlightService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class FlightsController extends BaseController {

    private final FlightService flightService;
    private final IFlightRepository flightRepository;
    private final IAirportRepository airportRepository;
    private final IAircraftRepository aircraftRepository;
    private final ICrewRepository crewRepository;

    public FlightsController() {
        super();
        this.flightRepository = RepositoryProvider.getFlightRepository();
        this.airportRepository = RepositoryProvider.getAirportRepository();
        this.aircraftRepository = RepositoryProvider.getAircraftRepository();
        this.crewRepository = RepositoryProvider.getCrewRepository();
        this.flightService = new FlightService(flightRepository, aircraftRepository, airportRepository);
    }

    public void initialize(TableView<Flight> flightsTable,
                           ComboBox<Airport> depAirports, ComboBox<Airport> arrAirports,
                           ComboBox<Aircraft> airCraftsNames, ComboBox<Crew> crewNames) {
        
        // Populate Dropdowns
        depAirports.setItems(airportRepository.getAll());
        arrAirports.setItems(airportRepository.getAll());
        airCraftsNames.setItems(aircraftRepository.getAll());
        crewNames.setItems(crewRepository.getAll());

        depAirports.setConverter(createAirportConverter());
        arrAirports.setConverter(createAirportConverter());
        airCraftsNames.setConverter(createAircraftConverter());
        crewNames.setConverter(createCrewConverter());

        // Setup Table
        setupTableColumns(flightsTable);
        flightsTable.setItems(flightRepository.getAll());
    }

    // --- Action (Pure Logic) ---
    public ServiceResult addFlight(FlightRequest request) {
        return flightService.addFlight(request);
    }

    // --- Helpers ---
    public void onAircraftSelected(Aircraft selectedAircraft, Label seatsLabel) {
        if (selectedAircraft != null) {
            seatsLabel.setText(String.valueOf(selectedAircraft.capacity()));
        } else {
            seatsLabel.setText("0");
        }
    }

    private void setupTableColumns(TableView<Flight> flightsTable) {
        TableColumn<Flight, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().id()));

        TableColumn<Flight, String> depPortCol = new TableColumn<>("Dep. Airport");
        depPortCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(airportRepository.findById(cellData.getValue().departureAirportId()).name())
        );

        TableColumn<Flight, String> arrPortCol = new TableColumn<>("Arr. Airport");
        arrPortCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(airportRepository.findById(cellData.getValue().arrivalAirportId()).name())
        );

        TableColumn<Flight, LocalDate> depTimeCol = new TableColumn<>("Dep. Time");
        depTimeCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().departureTime()));

        TableColumn<Flight, LocalDate> arrTimeCol = new TableColumn<>("Arr. Time");
        arrTimeCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().arrivalTime()));
        
        TableColumn<Flight, String> crewCol = new TableColumn<>("Crew");
        crewCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(crewRepository.findById(cellData.getValue().crewId()).crewName())
        );

        flightsTable.getColumns().setAll(idCol, depPortCol, arrPortCol, depTimeCol, arrTimeCol, crewCol);
        flightsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private StringConverter<Airport> createAirportConverter() {
        return new StringConverter<>() {
            @Override public String toString(Airport airport) {
                return airport == null ? null : airport.name() + " (" + airport.location() + ")";
            }
            @Override public Airport fromString(String string) { return null; }
        };
    }
    
    private StringConverter<Aircraft> createAircraftConverter() {
        return new StringConverter<>() {
            @Override public String toString(Aircraft aircraft) {
                return aircraft == null ? null : aircraft.type() + " (Cap: " + aircraft.capacity() + ")";
            }
            @Override public Aircraft fromString(String string) { return null; }
        };
    }

    private StringConverter<Crew> createCrewConverter() {
        return new StringConverter<>() {
            @Override public String toString(Crew crew) {
                return crew == null ? null : crew.crewName() + " (" + crew.captainName() + ")";
            }
            @Override public Crew fromString(String string) { return null; }
        };
    }
}