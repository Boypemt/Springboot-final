package th.mfu.pvz.catalog.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.pvz.catalog.domain.PlantClass;
import th.mfu.pvz.catalog.dto.PlantClassDTO;

@Mapper
public interface PlantClassMapper {

    PlantClassDTO toDTO(PlantClass plantClass);

    PlantClass toEntity(PlantClassDTO plantClassDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PlantClassDTO dto, @MappingTarget PlantClass entity);
}
