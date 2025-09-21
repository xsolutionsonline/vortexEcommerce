package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {OrderItemPersistenceMapper.class})
public interface OrderPersistenceMapper {

    Order toDomain(OrderEntity orderEntity);

    OrderEntity toEntity(Order order);

    /**
     * Después de mapear un Order a un OrderEntity, este método establece el vínculo bidireccional
     * entre OrderEntity y sus hijos OrderItemEntity. Esto es crucial para que JPA
     * persista correctamente la clave foránea 'order_id' en la tabla 'order_items'.
     */
    @AfterMapping
    default void establishBidirectionalRelationship(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(itemEntity -> itemEntity.setOrder(orderEntity));
        }
    }
}