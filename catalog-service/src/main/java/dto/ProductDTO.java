package dto;

import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private Long plantId;
    private String plantName;
    private String className;
    private String environmentName;
    private BigDecimal price;
    private Integer stock;
    private Integer servedBy; // เก็บหมายเลข Port (เช่น 8100/8101) สำหรับ Load Balancer Demo
    
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
    public String getPlantName() {
        return plantName;
    }
    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
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
    public Integer getServedBy() {
        return servedBy;
    }
    public void setServedBy(Integer servedBy) {
        this.servedBy = servedBy;
    }
    public String getEnvironmentName() {
        return environmentName;
    }
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    
}
