package th.mfu.pvz.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * catalog-service - owned by Member 2 - ปัณณวิชญ์ สิทธิตัน (682110181)
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }
}
