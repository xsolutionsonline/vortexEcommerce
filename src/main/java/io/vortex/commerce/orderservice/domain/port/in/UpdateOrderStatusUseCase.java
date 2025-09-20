package io.vortex.commerce.orderservice.domain.port.in;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderStatus;

public interface UpdateOrderStatusUseCase {
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);
}