package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper;

import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemPersistenceMapper {

    @Mapping(target = "order", ignore = true) // Evita la dependencia circular al mapear a dominio
    OrderItem toDomain(OrderItemEntity orderItemEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true) // Avoids circular dependency during mapping
    OrderItemEntity toEntity(OrderItem orderItem);
}