package io.vortex.commerce.orderservice.infrastructure.adapter.out.product;

import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCacheAdapter {

    private final JpaProductRepository productRepository;

    @Cacheable(value = "products", key = "#productId")
    public Optional<ProductEntity> findProductById(Long productId) {
        log.info("Fetching product {} from database", productId);
        return productRepository.findById(productId);
    }
}