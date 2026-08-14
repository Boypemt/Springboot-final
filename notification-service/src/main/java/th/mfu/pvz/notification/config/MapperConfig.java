package th.mfu.pvz.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.mfu.pvz.notification.dto.NotificationMapper;

/**
 * Registered explicitly instead of via @Component. Component-scanning a
 * class in this package raced with Spring Data's repository-interface
 * scanner in this project and intermittently dropped a bean definition.
 */
@Configuration
public class MapperConfig {

    @Bean
    public NotificationMapper notificationMapper() {
        return new NotificationMapper();
    }
}
