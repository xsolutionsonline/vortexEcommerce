package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto;

import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        LocalDateTime orderDate,
        OrderStatus status,
        BigDecimal totalPrice,
        int version,
        List<OrderItemResponse> items
) {}
