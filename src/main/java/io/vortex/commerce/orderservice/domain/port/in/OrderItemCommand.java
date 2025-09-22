package io.vortex.commerce.orderservice.domain.port.in;

public record OrderItemCommand(Long productId, Integer quantity) {
}