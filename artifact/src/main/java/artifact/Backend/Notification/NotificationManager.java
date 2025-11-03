package artifact.Backend.Notification;

import artifact.Backend.Models.Notification;
import artifact.Backend.Services.Impl.EmailNotificationService;
import artifact.Backend.Services.Interfaces.INotificationObserver;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private static NotificationManager instance;
    private final List<INotificationObserver> observers = new ArrayList<>();

    private NotificationManager() {
        // Register default services
        // In a perfect DIP world, we would inject this too, but for this scale, this is fine.
        EmailNotificationService service = new EmailNotificationService();
        this.subscribe(service);
    }

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    public void subscribe(INotificationObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(INotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyAll(Notification notification) {
        for (INotificationObserver observer : observers) {
            observer.onNotify(notification);
        }
    }
}