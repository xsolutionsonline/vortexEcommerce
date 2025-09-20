package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, Integer quantity, BigDecimal price) {}
