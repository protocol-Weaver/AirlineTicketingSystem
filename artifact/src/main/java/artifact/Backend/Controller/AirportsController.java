package artifact.Backend.Controller;

import artifact.Backend.Models.Airport;
import artifact.Backend.Models.DTO.AirportRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IAirportRepository;
import artifact.Backend.Services.Impl.AirportService;
import artifact.Backend.Services.Interfaces.IAirportService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AirportsController extends BaseController {

    private final IAirportService airportService;
    private final IAirportRepository airportRepository;
    
    // FIX: Removed the "private TextField..." fields that were here. 
    // The Controller does NOT own UI components.

    public AirportsController() {
        super();
        this.airportRepository = RepositoryProvider.getAirportRepository();
        // Ideally, use a ServiceProvider, but for now specific instantiation is okay
        this.airportService = new AirportService(airportRepository);
    }

    // --- Initialization ---
    public void initialize(TableView<Airport> airportsTable) {
        TableColumn<Airport, Long> idCol = new TableColumn<>("Airport ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<Airport, String> nameCol = new TableColumn<>("Airport Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));

        TableColumn<Airport, String> locationCol = new TableColumn<>("Airport Location");
        locationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().location()));

        airportsTable.getColumns().setAll(idCol, nameCol, locationCol);
        airportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        airportsTable.setItems(airportRepository.getAll());
    }

    // --- Actions ---
    public ServiceResult addAirport(AirportRequest request) {
        // Pure delegation. Controller takes DTO -> calls Service -> returns Result.
        return airportService.addAirport(request);
    }
}