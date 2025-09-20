package io.vortex.commerce.orderservice.infrastructure.adapter.in.web.controller;

import io.vortex.commerce.orderservice.domain.port.in.CreateOrderUseCase;
import io.vortex.commerce.orderservice.domain.port.in.UpdateOrderStatusUseCase;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.OrderResponse;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.UpdateStatusRequest;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.mapper.OrderApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
    private final OrderApiMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        var command = mapper.toCommand(request);
        var createdOrder = createOrderUseCase.createOrder(command);
        var response = mapper.toResponse(createdOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        var updatedOrder = updateOrderStatusUseCase.updateOrderStatus(orderId, request.newStatus());
        var response = mapper.toResponse(updatedOrder);

        return ResponseEntity.ok(response);
    }
}
