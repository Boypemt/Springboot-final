package th.mfu.pvz.inventory.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.mfu.pvz.inventory.dto.StockMovementDTO;
import th.mfu.pvz.inventory.dto.StockMovementMapper;
import th.mfu.pvz.inventory.service.InventoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock-movements")
public class StockMovementController {

    private final InventoryService inventoryService;
    private final StockMovementMapper mapper;

    public StockMovementController(InventoryService inventoryService, StockMovementMapper mapper) {
        this.inventoryService = inventoryService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<StockMovementDTO>> getAll() {
        List<StockMovementDTO> result = inventoryService.getAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
