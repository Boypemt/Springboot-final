package th.mfu.pvz.inventory;

import th.mfu.pvz.inventory.dto.OrderItemEvent;
import th.mfu.pvz.inventory.dto.OrderPlacedEvent;
import th.mfu.pvz.inventory.dto.ProductDTO;
import th.mfu.pvz.inventory.feign.CatalogClient;
import th.mfu.pvz.inventory.repository.StockMovementRepository;
import th.mfu.pvz.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Test
    void processOrderPlaced_callsCatalogClientForEachItem() {
        CatalogClient catalogClient = mock(CatalogClient.class);
        StockMovementRepository repository = mock(StockMovementRepository.class);
        when(catalogClient.updateStock(anyLong(), any())).thenReturn(new ProductDTO());

        InventoryService service = new InventoryService(catalogClient, repository);

        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setOrderId(1L);
        event.setCustomerId(3L);
        event.setTotalPrice(new BigDecimal("450.00"));
        event.setOrderDate(LocalDateTime.now());

        OrderItemEvent item = new OrderItemEvent();
        item.setProductId(2L);
        item.setQty(3);
        item.setUnitPrice(new BigDecimal("150.00"));
        event.setItems(Collections.singletonList(item));

        service.processOrderPlaced(event);

        verify(catalogClient, times(1)).updateStock(eq(2L), any());
        verify(repository, times(1)).save(any());
    }

    @Test
    void processOrderPlaced_doesNotThrowWhenCatalogClientFails() {
        CatalogClient catalogClient = mock(CatalogClient.class);
        StockMovementRepository repository = mock(StockMovementRepository.class);
        when(catalogClient.updateStock(anyLong(), any())).thenThrow(new RuntimeException("catalog-service down"));

        InventoryService service = new InventoryService(catalogClient, repository);

        OrderPlacedEvent event = new OrderPlacedEvent();
        event.setOrderId(1L);
        OrderItemEvent item = new OrderItemEvent();
        item.setProductId(2L);
        item.setQty(3);
        event.setItems(Collections.singletonList(item));

        // A failed Feign call must not bubble up and crash the Kafka listener
        assertDoesNotThrow(() -> service.processOrderPlaced(event));
        verify(repository, times(1)).save(any());
    }
}
