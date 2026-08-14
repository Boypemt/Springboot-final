package th.mfu.pvz.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import th.mfu.pvz.inventory.domain.StockMovement;
import th.mfu.pvz.inventory.dto.OrderItemEvent;
import th.mfu.pvz.inventory.dto.OrderPlacedEvent;
import th.mfu.pvz.inventory.dto.StockUpdateRequest;
import th.mfu.pvz.inventory.feign.CatalogClient;
import th.mfu.pvz.inventory.repository.StockMovementRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final CatalogClient catalogClient;
    private final StockMovementRepository repository;

    public InventoryService(CatalogClient catalogClient, StockMovementRepository repository) {
        this.catalogClient = catalogClient;
        this.repository = repository;
    }

    /**
     * Called by the Kafka listener for every OrderPlaced event.
     * One item can fail without blocking the others - each gets its own
     * try/catch and its own StockMovement row (status OK or FAILED), so a
     * catalog-service hiccup on one product doesn't hide what happened to
     * the rest of the order.
     */
    public void processOrderPlaced(OrderPlacedEvent event) {
        List<OrderItemEvent> items = event.getItems();
        if (items == null || items.isEmpty()) {
            log.warn("OrderPlaced event for orderId={} has no items, nothing to deduct", event.getOrderId());
            return;
        }

        for (OrderItemEvent item : items) {
            deductStockForItem(event.getOrderId(), item);
        }
    }

    private void deductStockForItem(Long orderId, OrderItemEvent item) {
        try {
            catalogClient.updateStock(item.getProductId(), new StockUpdateRequest(-item.getQty()));
            saveMovement(orderId, item, "OK", null);
            log.info("Deducted stock: orderId={} productId={} qty={}", orderId, item.getProductId(), item.getQty());
        } catch (Exception e) {
            // Don't rethrow - a failed Feign call here shouldn't crash the
            // Kafka listener container. Log it as a FAILED movement so it's
            // visible via GET /api/stock-movements instead of silently lost.
            saveMovement(orderId, item, "FAILED", e.getMessage());
            log.error("Failed to deduct stock: orderId={} productId={}", orderId, item.getProductId(), e);
        }
    }

    private void saveMovement(Long orderId, OrderItemEvent item, String status, String detail) {
        StockMovement movement = new StockMovement(
                orderId,
                item.getProductId(),
                item.getQty(),
                status,
                detail,
                LocalDateTime.now()
        );
        repository.save(movement);
    }

    public List<StockMovement> getAll() {
        return (List<StockMovement>) repository.findAll();
    }
}
