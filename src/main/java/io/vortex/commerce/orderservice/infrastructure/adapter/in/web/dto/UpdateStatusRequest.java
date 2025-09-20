package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto;

import io.vortex.commerce.orderservice.domain.model.OrderStatus;

public record UpdateStatusRequest(OrderStatus newStatus) {}
