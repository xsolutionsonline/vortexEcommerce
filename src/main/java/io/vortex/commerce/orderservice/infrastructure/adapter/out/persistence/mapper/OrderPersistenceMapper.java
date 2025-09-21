package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {
    OrderEntity toEntity(Order order);
    Order toDomain(OrderEntity orderEntity);
}