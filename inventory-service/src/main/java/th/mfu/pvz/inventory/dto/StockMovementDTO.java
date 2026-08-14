package th.mfu.pvz.inventory.dto;

import java.time.LocalDateTime;

public class StockMovementDTO {

    private Long id;
    private Long orderId;
    private Long productId;
    private Integer qtyDeducted;
    private String status;
    private String detail;
    private LocalDateTime movementDate;

    public StockMovementDTO() {
    }

    public StockMovementDTO(Long id, Long orderId, Long productId, Integer qtyDeducted,
                             String status, String detail, LocalDateTime movementDate) {
        this.id = id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQtyDeducted() {
        return qtyDeducted;
    }

    public void setQtyDeducted(Integer qtyDeducted) {
        this.qtyDeducted = qtyDeducted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDateTime movementDate) {
        this.movementDate = movementDate;
    }
}
