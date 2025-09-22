package io.vortex.commerce.orderservice.domain.services;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import io.vortex.commerce.orderservice.domain.exception.ConcurrencyConflictException;
import io.vortex.commerce.orderservice.domain.exception.OrderNotFoundException;
import io.vortex.commerce.orderservice.domain.exception.ProductNotFoundException;
import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import io.vortex.commerce.orderservice.domain.model.Product;
import io.vortex.commerce.orderservice.domain.port.in.CreateOrderCommand;
import io.vortex.commerce.orderservice.domain.port.in.FindOrdersUseCase;
import io.vortex.commerce.orderservice.domain.port.in.OrderItemCommand;
import io.vortex.commerce.orderservice.domain.port.out.InventoryPort;
import io.vortex.commerce.orderservice.domain.port.out.OrderRepositoryPort;
import io.vortex.commerce.orderservice.domain.port.out.ProductPort;
import io.vortex.commerce.orderservice.domain.service.OrderService;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.kafka.OrderEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;
    @Mock
    private InventoryPort inventoryPort;
    @Mock
    private ProductPort productPort;
    @Mock
    private OrderEventProducer eventProducer;

    @InjectMocks
    private OrderService orderService;

    private OrderItemCommand itemCommand;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        itemCommand = new OrderItemCommand(1L, 2);
        product = new Product(1L, "Test Product", new BigDecimal("50.00"));
        order = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .customerId(123L)
                .items(List.of(OrderItem.builder().productId(1L).quantity(2).price(product.price()).build()))
                .version(0)
                .build();
    }

    @Test
    @DisplayName("createOrder should create order successfully")
    void createOrder_shouldCreateOrderSuccessfully() {
        // Arrange
        CreateOrderCommand command = new CreateOrderCommand(123L, List.of(itemCommand));
        when(productPort.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryPort.hasSufficientStock(anyList())).thenReturn(true);
        when(orderRepositoryPort.save(any(Order.class))).thenReturn(order);

        // Act
        Order result = orderService.createOrder(command);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(123L, result.getCustomerId());

        verify(inventoryPort).hasSufficientStock(anyList());
        verify(inventoryPort).reserveStock(anyList());
        verify(orderRepositoryPort).save(any(Order.class));

        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);
        verify(eventProducer).sendOrderEvent(eventCaptor.capture());
        assertEquals(OrderEvent.EventType.CREATED, eventCaptor.getValue().getEventType());
        assertEquals(order, eventCaptor.getValue().getPayload());
    }

    @Test
    @DisplayName("createOrder should throw ProductNotFoundException when product does not exist")
    void createOrder_shouldThrowProductNotFoundException() {
        // Arrange
        CreateOrderCommand command = new CreateOrderCommand(123L, List.of(itemCommand));
        when(productPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(command));
        assertEquals(String.format(ErrorMessages.PRODUCT_NOT_FOUND, 1L), exception.getMessage());
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("createOrder should throw IllegalStateException for insufficient stock")
    void createOrder_shouldThrowExceptionForInsufficientStock() {
        // Arrange
        CreateOrderCommand command = new CreateOrderCommand(123L, List.of(itemCommand));
        when(productPort.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryPort.hasSufficientStock(anyList())).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> orderService.createOrder(command));
        assertEquals(ErrorMessages.INSUFFICIENT_STOCK_FOR_PRODUCT, exception.getMessage());
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("updateOrderStatus should process a PENDING order")
    void updateOrderStatus_shouldProcessPendingOrder() {
        // Arrange
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

        // Assert
        assertEquals(OrderStatus.PROCESSING, result.getStatus());
        verify(inventoryPort, never()).releaseStock(anyList());
        verify(orderRepositoryPort).save(order);
        verify(eventProducer).sendOrderEvent(any(OrderEvent.class));
    }

    @Test
    @DisplayName("updateOrderStatus should cancel a PENDING order and release stock")
    void updateOrderStatus_shouldCancelOrderAndReleaseStock() {
        // Arrange
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        // Assert
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(inventoryPort).releaseStock(order.getItems());
        verify(orderRepositoryPort).save(order);
        verify(eventProducer).sendOrderEvent(any(OrderEvent.class));
    }

    @Test
    @DisplayName("updateOrderStatus should throw OrderNotFoundException")
    void updateOrderStatus_shouldThrowOrderNotFoundException() {
        // Arrange
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(1L, OrderStatus.PROCESSING));
        assertEquals(String.format(ErrorMessages.ORDER_NOT_FOUND, 1L), exception.getMessage());
    }

    @Test
    @DisplayName("updateOrderStatus should throw ConcurrencyConflictException")
    void updateOrderStatus_shouldThrowConcurrencyConflictException() {
        // Arrange
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(order)).thenThrow(new ObjectOptimisticLockingFailureException("Concurrency error", null));

        // Act & Assert
        ConcurrencyConflictException exception = assertThrows(ConcurrencyConflictException.class, () -> orderService.updateOrderStatus(1L, OrderStatus.PROCESSING));
        assertEquals(ErrorMessages.CONCURRENCY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("findById should return order when found")
    void findById_shouldReturnOrderWhenFound() {
        // Arrange
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(order, result.get());
    }

    @Test
    @DisplayName("findOrders should return a page of orders")
    void findOrders_shouldReturnPageOfOrders() {
        // Arrange
        FindOrdersUseCase.FindOrdersQuery query = new FindOrdersUseCase.FindOrdersQuery(null, null, null,null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> expectedPage = new PageImpl<>(List.of(order), pageable, 1);
        when(orderRepositoryPort.findByQuery(query, pageable)).thenReturn(expectedPage);

        // Act
        Page<Order> result = orderService.findOrders(query, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(order, result.getContent().get(0));
    }

    @Test
    @DisplayName("cancelOrder should cancel an order successfully")
    void cancelOrder_shouldCancelOrderSuccessfully() {
        // Arrange
        when(orderRepositoryPort.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepositoryPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Order result = orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(inventoryPort).releaseStock(order.getItems());
        verify(orderRepositoryPort).save(order);
        verify(eventProducer).sendOrderEvent(any(OrderEvent.class));
    }

    @Test
    @DisplayName("cancelOrder should throw OrderNotFoundException when order not found")
    void cancelOrder_shouldThrowExceptionWhenNotFound() {
        // Arrange
        Long nonExistentOrderId = 99L;
        when(orderRepositoryPort.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        // Act & Assert
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(nonExistentOrderId));
        assertEquals(String.format(ErrorMessages.ORDER_NOT_FOUND, nonExistentOrderId), exception.getMessage());
    }
}