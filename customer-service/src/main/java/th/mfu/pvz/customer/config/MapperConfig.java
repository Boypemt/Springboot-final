package th.mfu.pvz.customer.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.mfu.pvz.customer.dto.mapper.AddressMapper;
import th.mfu.pvz.customer.dto.mapper.CustomerMapper;

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
    public AddressMapper addressMapper() {
        return Mappers.getMapper(AddressMapper.class);
    }

    @Bean
    public CustomerMapper customerMapper() {
        return Mappers.getMapper(CustomerMapper.class);
    }
}
