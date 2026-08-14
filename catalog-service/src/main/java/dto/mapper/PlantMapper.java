package dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import domain.Plant;
import dto.PlantDTO;

@Mapper(componentModel = "spring")
public interface PlantMapper {

    @Mapping(source = "plantClass.id", target = "classId")
    @Mapping(source = "plantClass.classname", target = "className")
    @Mapping(source = "environment.id", target = "environmentId")
    @Mapping(source = "environment.envname", target = "environmentName")
    PlantDTO toDTO(Plant plant);

    Plant toEntity(PlantDTO plantDTO);

    // สำหรับคำสั่ง PATCH
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PlantDTO dto, @MappingTarget Plant entity);
}
