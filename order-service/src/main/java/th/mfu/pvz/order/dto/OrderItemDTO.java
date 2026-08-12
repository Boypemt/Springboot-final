package th.mfu.pvz.order.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One order line ON THE WIRE.
 *
 * On the way IN (POST /api/orders) the client sends only productId and qty.
 * On the way OUT we add productName (fetched from catalog-service) and
 * lineTotal (computed here), so the browser can draw the order without a second
 * request.
 *
 * Every field is an object type, never a primitive - the PATCH mapping works by
 * skipping fields that are null, and an int can never be null.
 */
public class OrderItemDTO {

    private Long id;

    @JsonProperty("productId")
    private Long productId;

    /** read-only, comes from catalog-service */
    private String productName;

    private Integer qty;

    private BigDecimal unitPrice;

    /** read-only, qty x unitPrice */
    private BigDecimal lineTotal;

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

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
