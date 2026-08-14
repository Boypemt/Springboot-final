package th.mfu.pvz.inventory.repository;

import org.springframework.data.repository.CrudRepository;

import th.mfu.pvz.inventory.domain.StockMovement;

public interface StockMovementRepository extends CrudRepository<StockMovement, Long> {
    
}
