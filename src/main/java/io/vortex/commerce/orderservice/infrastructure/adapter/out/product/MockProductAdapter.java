package io.vortex.commerce.orderservice.infrastructure.adapter.out.product;

import io.vortex.commerce.orderservice.domain.model.Product;
import io.vortex.commerce.orderservice.domain.port.out.ProductPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MockProductAdapter implements ProductPort {

    private static final Map<Long, Product> productCatalog = new ConcurrentHashMap<>();

    static {

        productCatalog.put(101L, new Product(101L, "Laptop Pro", new BigDecimal("1200.00")));
        productCatalog.put(102L, new Product(102L, "Wireless Mouse", new BigDecimal("25.50")));
        productCatalog.put(103L, new Product(103L, "Mechanical Keyboard", new BigDecimal("150.75")));
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(productCatalog.get(productId));
    }
}