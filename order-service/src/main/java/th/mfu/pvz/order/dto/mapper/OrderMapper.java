package th.mfu.pvz.order.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.mfu.pvz.order.domain.Order;
import th.mfu.pvz.order.dto.OrderDTO;

/**
 * The Assembler for Order.
 *
 * I write the interface; MapStruct writes the class at COMPILE time. After a
 * build you can read it in
 *   order-service/target/generated-sources/annotations/.../OrderMapperImpl.java
 * and it is nothing but plain getters and setters - no reflection, no runtime
 * magic. componentModel = "spring" makes it a @Component so it can be
 * @Autowired.
 *
 * uses = OrderItemMapper.class tells MapStruct how to turn List<OrderItem> into
 * List<OrderItemDTO>: it calls the other mapper per element instead of me
 * writing a loop.
 */
@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {

    /** Entity -> DTO. customerName and servedBy come from other services. */
    @Mapping(target = "customerName", ignore = true)
    @Mapping(target = "servedBy", ignore = true)
    OrderDTO toDto(Order entity);

    /**
     * DTO -> EXISTING entity, for PATCH. This is the important one.
     *
     * nullValuePropertyMappingStrategy = IGNORE means: if a field of the DTO is
     * null, do not touch the entity. So a PATCH body of {"status":"shipped"}
     * changes the status and leaves the date, the total and the lines exactly as
     * they were. Without IGNORE those fields would be copied over as null and
     * the update would quietly wipe them.
     *
     * id, customerId and items are ignored because the controller decides them:
     * the id comes from the URL, and the customer and the lines must be
     * re-validated against the other services rather than trusted from a body.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "items", ignore = true)
    void updateOrderFromDto(OrderDTO dto, @MappingTarget Order entity);
}
