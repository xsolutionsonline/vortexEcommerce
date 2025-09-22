package io.vortex.commerce.orderservice.domain.port.out;

import io.vortex.commerce.orderservice.domain.model.Product;

import java.util.Optional;

public interface ProductPort {
    Optional<Product> findById(Long productId);
}