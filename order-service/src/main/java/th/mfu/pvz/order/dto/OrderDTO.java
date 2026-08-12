package th.mfu.pvz.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * An order ON THE WIRE.
 *
 * Three things worth pointing at during the demo:
 *
 * 1. customerName does not exist in this service's database. It arrives from
 *    customer-service while the request is being handled.
 * 2. servedBy is the port of the catalog-service INSTANCE that answered. It is
 *    how the load balancer becomes visible: call POST /api/orders repeatedly and
 *    it alternates 8100, 8101, 8100...
 * 3. The entity is never serialized. Only this class is.
 */
public class OrderDTO {

    private Long id;

    private Long customerId;

    /** read-only, comes from customer-service */
    private String customerName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    private BigDecimal totalPrice;

    private String status;

    /** which catalog-service instance answered - the load balancer, made visible */
    private Integer servedBy;

    private List<OrderItemDTO> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getServedBy() {
        return servedBy;
    }

    public void setServedBy(Integer servedBy) {
        this.servedBy = servedBy;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
