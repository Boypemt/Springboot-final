package th.mfu.pvz.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Mirrors the event order-service publishes to the "orders" Kafka topic.
 * See README.md section 4 ("Kafka (pub/sub)") for the payload shape.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPlacedEvent {

    private Long orderId;
    private Long customerId;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private List<OrderItemEvent> items;

    public OrderPlacedEvent() {
        // Jackson
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }
}
