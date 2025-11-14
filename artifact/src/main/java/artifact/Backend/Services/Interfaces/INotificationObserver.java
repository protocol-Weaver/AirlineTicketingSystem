package artifact.Backend.Services.Interfaces;
import artifact.Backend.Models.Notification;

public interface INotificationObserver {
    void onNotify(Notification notification);
}