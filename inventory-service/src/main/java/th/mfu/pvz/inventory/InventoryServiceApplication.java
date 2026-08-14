package th.mfu.pvz.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * inventory-service - owned by Member 4 - สายกลาง จะวะนะ (682110198)
 *
 * TODO: @KafkaListener(groupId = "inventory-group") -> cut stock via
 *       CatalogClient.adjustStock(). See TASKS.md section 4, Member 4.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaRepositories(basePackages = "th.mfu.pvz.inventory.repository")
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
