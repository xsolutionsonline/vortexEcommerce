package io.vortex.commerce.orderservice.domain.port.in;

import java.util.List;
import io.vortex.commerce.orderservice.domain.model.Order;

public interface CreateOrderUseCase {
    Order createOrder(CreateOrderCommand command);
    record CreateOrderCommand(Long customerId, List<OrderItemCommand> items) {}
    record OrderItemCommand(Long productId, Integer quantity) {}
}
