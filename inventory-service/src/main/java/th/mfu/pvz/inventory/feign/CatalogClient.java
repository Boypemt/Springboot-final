package th.mfu.pvz.inventory.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import th.mfu.pvz.inventory.dto.ProductDTO;
import th.mfu.pvz.inventory.dto.StockUpdateRequest;

/**
 * "name" must match catalog-service's spring.application.name exactly -
 * that's the string Eureka uses to resolve which instances to load-balance
 * across (8100 / 8101). Confirm the exact name with ปัณณวิชญ์ if unsure.
 */
@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @PatchMapping("/api/products/{id}/stock")
    ProductDTO updateStock(@PathVariable("id") Long productId, @RequestBody StockUpdateRequest request);
}
