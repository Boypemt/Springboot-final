package th.mfu.pvz.order.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * One line of an order: "3 x Peashooter at 100.00 each".
 *
 * This is the WEAK ENTITY of our database design: it has no meaning without its
 * order, and it is deleted with it.
 *
 * unitPrice is copied from catalog-service at the moment of the sale, and never
 * read back afterwards. If the shop changes the price of a Peashooter tomorrow,
 * this order must still show what the customer actually paid. That is also why
 * storing it here is not a transitive dependency - see the 3NF proof in the
 * README.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** FK to catalog-service. A plain Long, for the same reason as customerId. */
    private Long productId;

    private Integer qty;

    private BigDecimal unitPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
