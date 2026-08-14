package th.mfu.pvz.inventory.dto;

/**
 * !!! ASSUMPTION - NOT YET CONFIRMED WITH catalog-service !!!
 *
 * This is the body inventory-service sends to:
 *   PATCH /api/products/{id}/stock
 *
 * Guessed contract: "qty" = how many units to SUBTRACT from current stock
 * (a delta, not the new absolute value). Talk to ปัณณวิชญ์ and confirm:
 *   1) the field name(s) catalog-service actually expects
 *   2) whether it's a delta to subtract, or the new absolute stock value
 * Then update this class (and InventoryService.callCatalogToDeductStock)
 * to match - everything else in this service stays the same either way.
 */
public class StockUpdateRequest {

    private Integer qty;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(Integer qty) {
        this.qty = qty;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
