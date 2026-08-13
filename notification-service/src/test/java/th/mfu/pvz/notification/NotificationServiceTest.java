package th.mfu.pvz.notification;

import org.junit.jupiter.api.Test;

import th.mfu.pvz.notification.Repository.NotificationRepository;
import th.mfu.pvz.notification.Service.NotificationService;
import th.mfu.pvz.notification.domain.Notification;
import th.mfu.pvz.notification.dto.OrderItemEvent;
import th.mfu.pvz.notification.dto.OrderPlacedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Test
    void recordOrderPlaced_savesNotificationWithOrderAndCustomerId() {
        NotificationRepository repository = mock(NotificationRepository.class);
        when(repository.save(org.mockito.ArgumentMatchers.any(Notification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NotificationService service = new NotificationService(repository);

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

        Notification result = service.recordOrderPlaced(event);

        assertEquals(1L, result.getOrderId());
        assertEquals(3L, result.getCustomerId());
    }
}
