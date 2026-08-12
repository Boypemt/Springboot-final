package th.mfu.pvz.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import th.mfu.pvz.order.dto.ProductDTO;

/**
 * How this service asks catalog-service for a product's price and stock.
 *
 * Because "catalog-service" is a name, Spring Cloud sends the call through its
 * load balancer: it asks Eureka for EVERY instance registered under that name
 * and picks one per call, round robin. Start catalog-service twice and the
 * servedBy field in the answer alternates between 8100 and 8101 - with no
 * configuration and no change in this file.
 */
@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable("id") Long id);
}
