package th.mfu.pvz.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * customer-service - owned by Member 3 - สิรวิชญ์ ยวงคำ (682110199)
 *
 * TODO: Customer + Address (One-to-Many). Never expose password in a DTO.
 *       See TASKS.md section 4, Member 3.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
