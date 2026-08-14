package th.mfu.pvz.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.mfu.pvz.inventory.dto.StockMovementMapper;

/**
 * Registered explicitly instead of via @Component. Component-scanning a
 * class in this package raced with Spring Data's repository-interface
 * scanner in this project and intermittently dropped a bean definition.
 */
@Configuration
public class MapperConfig {

    @Bean
    public StockMovementMapper stockMovementMapper() {
        return new StockMovementMapper();
    }
}
