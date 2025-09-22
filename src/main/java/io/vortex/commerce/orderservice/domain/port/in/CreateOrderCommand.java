package io.vortex.commerce.orderservice.domain.port.in;

import java.util.List;

public record CreateOrderCommand(Long customerId, List<OrderItemCommand> items) {
}