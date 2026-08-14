package th.mfu.pvz.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Mirrors what GET/PATCH /api/products/{id} on catalog-service returns.
 * README section 4 mentions every catalog response carries a "servedBy" field
 * (the port that answered) for the load-balancer demo - included here so it's
 * not silently dropped, but inventory-service doesn't otherwise use it.
 *
 * ASSUMPTION - confirm the real field names with ปัณณวิชญ์ (catalog-service owner)
 * once that endpoint exists, and adjust this class to match.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private Long id;
    private Long plantId;
    private BigDecimal price;
    private Integer stock;
    private String servedBy;

    public ProductDTO() {
        // Jackson
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getServedBy() {
        return servedBy;
    }

    public void setServedBy(String servedBy) {
        this.servedBy = servedBy;
    }
}

