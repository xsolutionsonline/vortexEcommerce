package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.mapper;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.port.in.CreateOrderCommand;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderApiMapper {
    CreateOrderCommand toCommand(CreateOrderRequest request);
    OrderResponse toResponse(Order order);
}