package th.mfu.pvz.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * catalog-service - owned by Member 2 - ปัณณวิชญ์ สิทธิตัน (682110181)
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"th.mfu.pvz.catalog", "domain", "dto", "repository", "controller"})
@EntityScan(basePackages = {"domain", "classes"})
@EnableJpaRepositories(basePackages = {"repository"})
public class CatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }
}
