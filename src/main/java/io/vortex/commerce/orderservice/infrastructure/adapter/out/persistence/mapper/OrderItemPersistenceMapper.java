package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper;

import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemPersistenceMapper {

    @Mapping(target = "order", ignore = true) 
    OrderItem toDomain(OrderItemEntity orderItemEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true) 
    OrderItemEntity toEntity(OrderItem orderItem);
}