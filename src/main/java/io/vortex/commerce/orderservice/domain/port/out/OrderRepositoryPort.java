package io.vortex.commerce.orderservice.domain.port.out;

import java.util.Optional;

import io.vortex.commerce.orderservice.domain.model.Order;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);    
}
