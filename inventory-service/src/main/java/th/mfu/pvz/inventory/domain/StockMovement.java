package th.mfu.pvz.inventory.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * NOT one of the 8 logical tables from the ERD. This is inventory-service's
 * own operational log - "what did the consumer do" - so GET /api/stock-movements
 * has something to show. Real stock lives in catalog-service's Products table.
 */
@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer qtyDeducted;

    @Column(nullable = false, length = 20)
    private String status; // e.g. "OK" or "FAILED"

    @Column(length = 500)
    private String detail; // e.g. error message when status = FAILED

    @Column(nullable = false)
    private LocalDateTime movementDate;

    protected StockMovement() {
        // JPA
    }

    public StockMovement(Long orderId, Long productId, Integer qtyDeducted,
                          String status, String detail, LocalDateTime movementDate) {
        this.orderId = orderId;
        this.productId = productId;
        this.qtyDeducted = qtyDeducted;
        this.status = status;
        this.detail = detail;
        this.movementDate = movementDate;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQtyDeducted() {
        return qtyDeducted;
    }

    public String getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }
}
