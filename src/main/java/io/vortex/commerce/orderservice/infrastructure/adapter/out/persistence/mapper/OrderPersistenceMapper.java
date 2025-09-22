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

    @AfterMapping
    default void establishBidirectionalRelationship(@MappingTarget OrderEntity orderEntity) {
        if (orderEntity.getItems() != null) {
            orderEntity.getItems().forEach(itemEntity -> itemEntity.setOrder(orderEntity));
        }
    }
}