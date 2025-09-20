package io.vortex.commerce.orderservice.domain.port.in;

import io.vortex.commerce.orderservice.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command);
}
