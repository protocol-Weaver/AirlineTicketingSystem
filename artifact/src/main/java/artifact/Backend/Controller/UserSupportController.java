package artifact.Backend.Controller;

import artifact.Backend.Models.DTO.TicketCreateRequest;
import artifact.Backend.Models.ServiceResult;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Services.Impl.SupportService;
import artifact.Backend.Services.Interfaces.ISupportService;
import javafx.collections.ObservableList;

public class UserSupportController extends BaseController {

    private final ISupportService supportService;

    public UserSupportController() {
        super();
        this.supportService = new SupportService();
    }

    /**
     * Get data for the view.
     */
    public ObservableList<SupportTicket> getMyTickets() {
        return supportService.getMyTickets(userSession.getCurrentUser());
    }

    /**
     * Handle creation action.
     */
    public ServiceResult createTicket(TicketCreateRequest request) {
        return supportService.createTicket(userSession.getCurrentUser(), request);
    }
}
