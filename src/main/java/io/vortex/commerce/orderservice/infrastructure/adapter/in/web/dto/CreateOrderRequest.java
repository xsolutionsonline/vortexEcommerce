package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto;

import java.util.List;

public record CreateOrderRequest(Long customerId, List<OrderItemRequest> items) {}