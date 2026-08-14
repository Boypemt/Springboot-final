package dto;

//ใช้เป็น Request Body สำหรับ API PATCH /api/products/{id}/stock เมื่อ inventory-service เรียกมาปรับเพิ่ม/ลดสต็อก
public class StockAdjustmentDTO {
    private Integer delta; // เช่น -3 (ลดสต็อก) หรือ 5 (เพิ่มสต็อก)

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    
}
