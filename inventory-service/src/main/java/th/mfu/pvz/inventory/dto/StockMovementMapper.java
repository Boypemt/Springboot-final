package th.mfu.pvz.inventory.dto;

import org.springframework.stereotype.Component;

import th.mfu.pvz.inventory.domain.StockMovement;

@Component
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
