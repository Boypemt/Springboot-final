package th.mfu.pvz.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * notification-service - owned by Member 4 - สายกลาง จะวะนะ (682110198)
 *
 * TODO: @KafkaListener(groupId = "notification-group") -> save a Notification.
 *       DIFFERENT group id from inventory-service, on purpose.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "th.mfu.pvz.notification.Repository")
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
