package th.mfu.pvz.notification.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import th.mfu.pvz.notification.Service.NotificationService;
import th.mfu.pvz.notification.dto.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public OrderEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    // groupId is ALSO set in application.properties (spring.kafka.consumer.group-id).
    // It's repeated here explicitly so this class stays correct even if the
    // properties file default ever changes - the two must never share a group
    // with inventory-service's "inventory-group".
    @KafkaListener(topics = "orders", groupId = "notification-group")
    public void onOrderPlaced(String message) {
        try {
            OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);
            notificationService.recordOrderPlaced(event);
        } catch (Exception e) {
            // Don't rethrow: an unparseable message shouldn't crash the listener
            // container or block the rest of the partition. Log and move on.
            log.error("Failed to process order event: {}", message, e);
        }
    }
}
