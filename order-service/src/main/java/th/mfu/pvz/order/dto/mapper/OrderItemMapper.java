package th.mfu.pvz.order.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import th.mfu.pvz.order.domain.OrderItem;
import th.mfu.pvz.order.dto.OrderItemDTO;

/**
 * productName and lineTotal are ignored here: one comes from another service
 * and the other is arithmetic. Neither is a copy, so neither belongs to the
 * mapper - the controller fills them in.
 */
@Mapper
public interface OrderItemMapper {

    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    OrderItemDTO toDto(OrderItem entity);
}
