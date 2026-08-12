package th.mfu.pvz.order.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * The announcement this service publishes on the Kafka topic "orders".
 *
 * THIS CLASS IS A CONTRACT (TASKS.md section 2). inventory-service and
 * notification-service parse exactly these field names. Changing one without
 * telling the group silently breaks both consumers - they will keep running and
 * simply read null.
 *
 * The event carries everything a subscriber needs. It does NOT say who should
 * react, or how many subscribers there are: order-service does not know and does
 * not care. That is what decoupled means.
 */
public class OrderPlacedEvent {

    private Long orderId;
    private Long customerId;
    private String customerName;
    private BigDecimal totalPrice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    private List<Item> items = new ArrayList<>();

    public OrderPlacedEvent() {
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    /** One line inside the event. */
    public static class Item {

        private Long productId;
        private String productName;
        private Integer qty;
        private BigDecimal unitPrice;

        public Item() {
        }

        public Item(Long productId, String productName, Integer qty, BigDecimal unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.qty = qty;
            this.unitPrice = unitPrice;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }
    }
}
