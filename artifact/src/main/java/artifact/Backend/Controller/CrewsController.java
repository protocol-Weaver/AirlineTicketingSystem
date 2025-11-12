package artifact.Backend.Controller;

import artifact.Backend.Models.Crew;
import artifact.Backend.Models.DTO.CrewRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Repositories.Interfaces.ICrewRepository;
import artifact.Backend.Services.Impl.CrewService;
import artifact.Backend.Services.Interfaces.ICrewService;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CrewsController extends BaseController {

    private final ICrewService crewService;
    private final ICrewRepository crewRepository;

    public CrewsController() {
        super();
        this.crewRepository = RepositoryProvider.getCrewRepository();
        this.crewService = new CrewService(crewRepository);
    }

    // --- Initialization ---
    public void initialize(TableView<Crew> crewTable) {
        TableColumn<Crew, Long> idCol = new TableColumn<>("Crew ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<Crew, String> nameCol = new TableColumn<>("Crew Name");
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().crewName()));

        TableColumn<Crew, String> cNameCol = new TableColumn<>("Captain Name");
        cNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().captainName()));

        crewTable.getColumns().setAll(idCol, nameCol, cNameCol);
        crewTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        crewTable.setItems(crewRepository.getAll());
    }

    // --- Action (Delegation) ---
    public ServiceResult addCrew(CrewRequest request) {
        return crewService.addCrew(request);
    }
}