package artifact.Backend.Repositories.Impl;

import artifact.Backend.Models.SupportMessage;
import artifact.Backend.Models.SupportTicket;
import artifact.Backend.Repositories.Interfaces.ISupportRepository;
import artifact.Backend.Tags.TicketStatus;
import artifact.Backend.Tags.UserRole;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SupportRepository extends BaseJsonRepository<SupportTicket> implements ISupportRepository {

    public SupportRepository() {
        super("support_tickets.json", new TypeToken<ArrayList<SupportTicket>>(){}.getType(), "/support_tickets", SupportTicket::id);
    }

    @Override
    public void add(SupportTicket item) {
        // Ensure ID is generated here
        SupportTicket newTicket = new SupportTicket(
            generateNextId(), item.userId(), item.userName(), item.subject(),
            TicketStatus.OPEN, item.createdAt(), new ArrayList<>(item.messages())
        );
        super.add(newTicket);
    }

    @Override
    public ObservableList<SupportTicket> findByUserId(long userId) {
        return dataList.stream()
                .filter(t -> t.userId() == userId)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    @Override
    public void addMessage(long ticketId, SupportMessage message) {
        SupportTicket oldTicket = findById(ticketId);
        if (oldTicket == null) return;

        TicketStatus newStatus = determineNewStatus(oldTicket.status(), message.senderRole());
        
        List<SupportMessage> newMessages = new ArrayList<>(oldTicket.messages());
        newMessages.add(message);

        SupportTicket updated = new SupportTicket(
            oldTicket.id(), oldTicket.userId(), oldTicket.userName(),
            oldTicket.subject(), newStatus, oldTicket.createdAt(), newMessages
        );
        update(updated);
    }

    // Extracted Business Logic (Testable!)
    private TicketStatus determineNewStatus(TicketStatus currentStatus, UserRole senderRole) {
        if (senderRole == UserRole.STAFF) {
            if (currentStatus == TicketStatus.OPEN || currentStatus == TicketStatus.RESOLVED) {
                return TicketStatus.IN_PROGRESS;
            }
        } else {
            if (currentStatus == TicketStatus.RESOLVED) {
                return TicketStatus.OPEN;
            }
        }
        return currentStatus;
    }

    // Custom method if you added it to the Interface
    public void updateStatus(long ticketId, TicketStatus status) {
        SupportTicket old = findById(ticketId);
        if (old != null) {
            SupportTicket updated = new SupportTicket(
                old.id(), old.userId(), old.userName(), old.subject(), 
                status, old.createdAt(), old.messages()
            );
            update(updated);
        }
    }
}