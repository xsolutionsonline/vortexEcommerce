package io.vortex.commerce.orderservice.domain.service;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import io.vortex.commerce.orderservice.domain.exception.OrderNotFoundException;
import io.vortex.commerce.orderservice.domain.exception.ProductNotFoundException;
import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import io.vortex.commerce.orderservice.domain.port.in.CreateOrderUseCase;
import io.vortex.commerce.orderservice.domain.port.in.CreateOrderCommand;
import io.vortex.commerce.orderservice.domain.port.in.UpdateOrderStatusUseCase;
import io.vortex.commerce.orderservice.domain.port.out.InventoryPort;
import io.vortex.commerce.orderservice.domain.port.out.OrderRepositoryPort;
import io.vortex.commerce.orderservice.domain.port.out.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements CreateOrderUseCase, UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryPort inventoryPort;
    private final ProductPort productPort;

    @Transactional
    @Override
    public Order createOrder(CreateOrderCommand command) {
        List<OrderItem> orderItems = command.items().stream()
                .map(itemCmd -> {
                    var product = productPort.findById(itemCmd.productId()).orElseThrow(() ->
                            new ProductNotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, itemCmd.productId())));

                    return OrderItem.builder()
                        .productId(itemCmd.productId())
                        .quantity(itemCmd.quantity())
                        .price(product.price())
                        .build();
                })
                .collect(Collectors.toList());


        if (!inventoryPort.hasSufficientStock(orderItems)) {
            throw new IllegalStateException(ErrorMessages.INSUFFICIENT_STOCK);
        }

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customerId(command.customerId())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .items(orderItems)
                .totalPrice(total)
                .build();

        inventoryPort.reserveStock(orderItems);

        return orderRepositoryPort.save(order);
    }

    @Transactional
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        switch (newStatus) {
            case PROCESSING:
                order.process();
                break;
            case SHIPPED:
                order.ship();
                break;
            case DELIVERED:
                order.deliver();
                break;
            case CANCELLED:
                order.cancel();
                break;
            default:
                throw new IllegalArgumentException(String.format(ErrorMessages.INVALID_STATUS_TRANSITION, newStatus));
        }

        return orderRepositoryPort.save(order);
    }
}