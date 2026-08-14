package th.mfu.pvz.customer.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import th.mfu.pvz.customer.domain.Address;
import th.mfu.pvz.customer.dto.AddressDTO;

@Mapper
public interface AddressMapper {
    AddressDTO toDto(Address address);
    Address toEntity(AddressDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(AddressDTO dto, @MappingTarget Address address);
}
