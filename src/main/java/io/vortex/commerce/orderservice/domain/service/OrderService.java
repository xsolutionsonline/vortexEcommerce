package io.vortex.commerce.orderservice.domain.service;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import io.vortex.commerce.orderservice.domain.exception.ConcurrencyConflictException;
import io.vortex.commerce.orderservice.domain.exception.OrderNotFoundException;
import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import io.vortex.commerce.orderservice.domain.exception.ProductNotFoundException;
import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import io.vortex.commerce.orderservice.domain.port.in.*;
import io.vortex.commerce.orderservice.domain.port.out.InventoryPort;
import io.vortex.commerce.orderservice.domain.port.out.OrderRepositoryPort;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.kafka.OrderEventProducer;
import io.vortex.commerce.orderservice.domain.port.out.ProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@EnableCaching
public class OrderService implements CreateOrderUseCase, UpdateOrderStatusUseCase, FindOrderByIdUseCase, FindOrdersUseCase, CancelOrderUseCase  {

    private final OrderRepositoryPort orderRepositoryPort;
    private final InventoryPort inventoryPort;
    private final ProductPort productPort;
    private final OrderEventProducer eventProducer;

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
            throw new IllegalStateException(ErrorMessages.INSUFFICIENT_STOCK_FOR_PRODUCT);
        }

        Order order = Order.builder()
                .customerId(command.customerId())
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .items(orderItems)
                .build();

        inventoryPort.reserveStock(order.getItems());

        Order savedOrder = orderRepositoryPort.save(order);

        eventProducer.sendOrderEvent(new OrderEvent(null, null, OrderEvent.EventType.CREATED, savedOrder));

        return savedOrder;
    }

    @Transactional
    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        try {
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
                    inventoryPort.releaseStock(order.getItems());
                    break;
                default:
                    throw new IllegalArgumentException(String.format(ErrorMessages.INVALID_STATUS_TRANSITION, newStatus));
            }
    
            Order savedOrder = orderRepositoryPort.save(order);

            eventProducer.sendOrderEvent(new OrderEvent(null, null, OrderEvent.EventType.STATUS_UPDATED, savedOrder));

            return savedOrder;
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConcurrencyConflictException(ErrorMessages.CONCURRENCY_ERROR, ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long orderId) {
        return orderRepositoryPort.findById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findOrders(FindOrdersQuery query, Pageable pageable) {
        return orderRepositoryPort.findByQuery(query, pageable);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        order.cancel();
        inventoryPort.releaseStock(order.getItems());

        Order savedOrder = orderRepositoryPort.save(order);

        eventProducer.sendOrderEvent(new OrderEvent(null, null, OrderEvent.EventType.STATUS_UPDATED, savedOrder));

        return savedOrder;
    }
}