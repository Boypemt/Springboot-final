package th.mfu.pvz.inventory.dto;

public class StockUpdateRequest {

    private Integer delta;

    public StockUpdateRequest() {
    }

    public StockUpdateRequest(Integer delta) {
        this.delta = delta;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }
}
