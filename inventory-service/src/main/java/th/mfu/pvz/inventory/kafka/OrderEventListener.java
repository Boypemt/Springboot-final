package th.mfu.pvz.inventory.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.mfu.pvz.inventory.dto.OrderPlacedEvent;
import th.mfu.pvz.inventory.service.InventoryService;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public OrderEventListener(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    // groupId="inventory-group" - must stay DIFFERENT from notification-service's
    // "notification-group", or Kafka treats them as one subscriber and splits
    // the stream instead of both groups getting every event.
    @KafkaListener(topics = "orders", groupId = "inventory-group")
    public void onOrderPlaced(String message) {
        try {
            OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);
            inventoryService.processOrderPlaced(event);
        } catch (Exception e) {
            log.error("Failed to process order event: {}", message, e);
        }
    }
}

