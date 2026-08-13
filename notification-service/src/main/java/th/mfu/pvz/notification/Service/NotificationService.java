package th.mfu.pvz.notification.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import th.mfu.pvz.notification.Repository.NotificationRepository;
import th.mfu.pvz.notification.domain.Notification;
import th.mfu.pvz.notification.dto.OrderPlacedEvent;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    /**
     * Called by the Kafka listener for every OrderPlaced event.
     * One notification row per order.
     */
    public Notification recordOrderPlaced(OrderPlacedEvent event) {
        String message = buildMessage(event);
        Notification notification = new Notification(
                event.getOrderId(),
                event.getCustomerId(),
                message,
                LocalDateTime.now()
        );
        Notification saved = repository.save(notification);
        log.info("Recorded notification for orderId={} customerId={}", event.getOrderId(), event.getCustomerId());
        return saved;
    }

    public List<Notification> getAll() {
        return (List<Notification>) repository.findAll();
    }

    public Notification getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    private String buildMessage(OrderPlacedEvent event) {
        int itemCount = event.getItems() == null ? 0 : event.getItems().size();
        return String.format(
                "Your order #%d (%d item%s, total %.2f) has been placed.",
                event.getOrderId(),
                itemCount,
                itemCount == 1 ? "" : "s",
                event.getTotalPrice()
        );
    }
}
