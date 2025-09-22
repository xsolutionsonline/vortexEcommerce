package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository;

import io.vortex.commerce.orderservice.domain.model.Product;
import io.vortex.commerce.orderservice.domain.port.out.ProductPort;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class ProductAdapter implements ProductPort {

    private final JpaProductRepository productRepository;
    private final ProductPersistenceMapper productMapper;

    @Override
    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDomain);
    }
}