package th.mfu.pvz.catalog.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.pvz.catalog.domain.Product;
import th.mfu.pvz.catalog.dto.ProductDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(source = "plant.id", target = "plantId")
    @Mapping(source = "plant.name", target = "plantName")
    @Mapping(source = "plant.plantClass.classname", target = "className")
    @Mapping(source = "plant.environment.envname", target = "environmentName")
    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO productDTO);
    
    // สำหรับคำสั่ง PATCH (ข้ามฟิลด์ที่เป็น null)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product entity);
}
