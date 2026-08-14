package th.mfu.pvz.customer.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import th.mfu.pvz.customer.domain.Customer;
import th.mfu.pvz.customer.dto.CustomerDTO;
import th.mfu.pvz.customer.dto.CustomerRequestDTO;

@Mapper
public interface CustomerMapper {
    CustomerDTO toDto(Customer customer);
    Customer toEntity(CustomerRequestDTO request);
    void replace(CustomerRequestDTO request, @MappingTarget Customer customer);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(CustomerRequestDTO request, @MappingTarget Customer customer);
}
