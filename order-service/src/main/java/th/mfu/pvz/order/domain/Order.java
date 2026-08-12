package th.mfu.pvz.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * One purchase.
 *
 * The table is called "orders" because ORDER is a reserved word in SQL.
 *
 * TWO THINGS TO BE ABLE TO EXPLAIN AT THE DEMO
 *
 * 1. customerId is a plain Long, NOT a @ManyToOne Customer.
 *    The customer row lives in customer-service's own database, so there is no
 *    table here to join to. What a foreign key did inside one program now costs
 *    an HTTP call (see CustomerClient). That is the real price of splitting an
 *    application into services.
 *
 * 2. items uses cascade = ALL and orphanRemoval = true.
 *    In our database design OrderItems is a WEAK ENTITY - it cannot exist
 *    without its order, and the schema says ON DELETE CASCADE. These two
 *    settings are that rule, expressed in JPA: one save() writes the order and
 *    all its lines, and deleting the order deletes them.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK to customer-service. Validated over HTTP, not by the database. */
    private Long customerId;

    private LocalDateTime orderDate;

    private BigDecimal totalPrice;

    /** pending / paid / shipped / cancelled */
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
    }

    /** Keeps both sides of the one-to-many in step. */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
