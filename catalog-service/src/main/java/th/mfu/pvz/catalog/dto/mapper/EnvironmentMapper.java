package th.mfu.pvz.catalog.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.pvz.catalog.domain.Environment;
import th.mfu.pvz.catalog.dto.EnvironmentDTO;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {

    EnvironmentDTO toDTO(Environment environment);
    
    Environment toEntity(EnvironmentDTO environmentDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // เพื่อไม่ให้ทับ id เดิมกรณีไม่ได้ส่ง id มา
    void updateEntityFromDto(EnvironmentDTO dto, @MappingTarget Environment entity);
}
