package artifact.Backend.Controller;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

import artifact.GUI.TicketConversationView;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Services.Impl.SupportService;

/**
 * Controller for the Staff Support Dashboard.
 */
public class StaffSupportController extends BaseController {

    private final SupportService supportService;

    public StaffSupportController() {
        super();
        this.supportService = new SupportService();
    }

    public void initialize(TableView<SupportTicket> table) {
        // ... (Column setup remains the same) ...
        TableColumn<SupportTicket, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().id()).asObject());

        TableColumn<SupportTicket, String> userCol = new TableColumn<>("Customer");
        userCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().userName()));

        TableColumn<SupportTicket, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().subject()));

        TableColumn<SupportTicket, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().toString()));
        
        TableColumn<SupportTicket, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().createdAt()));

        // --- Action Column (Open Chat) ---
        TableColumn<SupportTicket, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button openBtn = new Button("Open Chat");
            {
                openBtn.setStyle("-fx-background-color: #00a4bf; -fx-text-fill: white; -fx-font-weight: bold;");
                openBtn.setOnAction(e -> {
                    SupportTicket ticket = getTableView().getItems().get(getIndex());
                    handleOpenChat(ticket, table);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : openBtn);
            }
        });

        table.getColumns().setAll(idCol, userCol, subjectCol, statusCol, dateCol, actionCol);
        
        // Fetch ALL tickets
        table.setItems(supportService.getAllTickets());
    }

    private void handleOpenChat(SupportTicket ticket, TableView<SupportTicket> table) {
        Stage chatStage = new Stage();
        chatStage.initModality(Modality.APPLICATION_MODAL);
        chatStage.setTitle("Support Chat - Ticket #" + ticket.id());
        
        // --- PASS CALLBACK ---
        // When ticket updates (e.g., Resolved), refresh the main table
        Runnable refreshCallback = () -> {
            table.refresh();
            table.setItems(supportService.getAllTickets());
        };
        
        TicketConversationView chatView = new TicketConversationView(ticket, this, refreshCallback);
        
        Scene scene = new Scene(chatView, 500, 600);
        chatStage.setScene(scene);
        
        // Also refresh on close just in case
        chatStage.setOnHidden(e -> refreshCallback.run());
        
        chatStage.show();
    }
    
    @Override
    public void goLogin() {
        super.goLogin();
    }
}