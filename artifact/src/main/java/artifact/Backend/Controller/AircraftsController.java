package artifact.Backend.Controller;

import artifact.Backend.Models.Aircraft;
import artifact.Backend.Models.DTO.AircraftRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.IAircraftRepository;
import artifact.Backend.Services.Impl.AircraftService;
import artifact.Backend.Services.Interfaces.IAircraftService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AircraftsController extends BaseController {

    private final IAircraftService aircraftService;
    private final IAircraftRepository aircraftRepository;

    public AircraftsController() {
        super();
        this.aircraftRepository = RepositoryProvider.getAircraftRepository();
        this.aircraftService = new AircraftService(aircraftRepository);
    }

    // --- Initialization (Table Setup) ---
    public void initialize(TableView<Aircraft> airCraftsTable) {
        TableColumn<Aircraft, Long> idCol = new TableColumn<>("Aircraft ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<Aircraft, String> nameCol = new TableColumn<>("Aircraft Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().type()));

        TableColumn<Aircraft, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().capacity()).asObject());

        airCraftsTable.getColumns().setAll(idCol, nameCol, capacityCol);
        airCraftsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        airCraftsTable.setItems(aircraftRepository.getAll());
    }

    // --- Action (Pure Delegation) ---
    public ServiceResult addAircraft(AircraftRequest request) {
        return aircraftService.addAircraft(request);
    }
}