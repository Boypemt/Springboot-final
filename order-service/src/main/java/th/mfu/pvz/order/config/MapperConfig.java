package th.mfu.pvz.order.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.mfu.pvz.order.dto.mapper.OrderItemMapper;
import th.mfu.pvz.order.dto.mapper.OrderMapper;

/**
 * The mappers are registered here explicitly instead of via
 * {@code @Mapper(componentModel = "spring")}. Component-scanning the
 * generated MapStruct impls turned out to race with Spring Data's
 * repository-interface scanner in this project and intermittently drop a
 * mapper's bean definition. Mappers.getMapper() sidesteps classpath
 * scanning entirely by loading the generated impl directly.
 */
@Configuration
public class MapperConfig {

    @Bean
    public OrderItemMapper orderItemMapper() {
        return Mappers.getMapper(OrderItemMapper.class);
    }

    @Bean
    public OrderMapper orderMapper() {
        return Mappers.getMapper(OrderMapper.class);
    }
}
