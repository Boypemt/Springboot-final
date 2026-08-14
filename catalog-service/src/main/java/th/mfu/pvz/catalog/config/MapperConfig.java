package th.mfu.pvz.catalog.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import th.mfu.pvz.catalog.dto.mapper.EnvironmentMapper;
import th.mfu.pvz.catalog.dto.mapper.PlantClassMapper;
import th.mfu.pvz.catalog.dto.mapper.PlantMapper;
import th.mfu.pvz.catalog.dto.mapper.ProductMapper;

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
    public EnvironmentMapper environmentMapper() {
        return Mappers.getMapper(EnvironmentMapper.class);
    }

    @Bean
    public PlantClassMapper plantClassMapper() {
        return Mappers.getMapper(PlantClassMapper.class);
    }

    @Bean
    public PlantMapper plantMapper() {
        return Mappers.getMapper(PlantMapper.class);
    }

    @Bean
    public ProductMapper productMapper() {
        return Mappers.getMapper(ProductMapper.class);
    }
}
