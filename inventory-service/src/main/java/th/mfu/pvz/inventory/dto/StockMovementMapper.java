package th.mfu.pvz.inventory.dto;

import th.mfu.pvz.inventory.domain.StockMovement;

public class StockMovementMapper {

    public StockMovementDTO toDTO(StockMovement entity) {
        if (entity == null) {
            return null;
        }
        return new StockMovementDTO(
                entity.getId(),
                entity.getOrderId(),
                entity.getProductId(),
                entity.getQtyDeducted(),
                entity.getStatus(),
                entity.getDetail(),
                entity.getMovementDate()
        );
    }
}
