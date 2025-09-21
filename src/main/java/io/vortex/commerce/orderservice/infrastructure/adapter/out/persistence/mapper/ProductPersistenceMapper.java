package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper;

import io.vortex.commerce.orderservice.domain.model.Product;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {
    Product toDomain(ProductEntity productEntity);

    ProductEntity toEntity(Product product);
}
