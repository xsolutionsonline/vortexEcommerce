package io.vortex.commerce.orderservice.domain.port.in;

import io.vortex.commerce.orderservice.domain.model.Order;
import java.util.Optional;

public interface FindOrderByIdUseCase {
    Optional<Order> findById(Long orderId);
}