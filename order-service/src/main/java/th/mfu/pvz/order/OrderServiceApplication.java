package th.mfu.pvz.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * order-service - owned by Member 1, เมธาสิทธิ์ พิบูลย์ศิลป์ (682110189)
 *
 * The only service that is both a CALLER and a PUBLISHER:
 *
 *   @EnableDiscoveryClient - register with Eureka, and look other services up
 *                            by name instead of by address
 *   @EnableFeignClients    - find the interfaces annotated @FeignClient and
 *                            build their implementations at startup. Nobody
 *                            writes those classes; Feign generates them from
 *                            the annotations.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaRepositories(basePackages = "th.mfu.pvz.order.repository")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
