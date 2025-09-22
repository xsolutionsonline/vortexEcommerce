package io.vortex.commerce.orderservice.domain.model;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    @DisplayName("Builder should create an order with all fields set correctly and cover getters")
    void builder_shouldCreateOrderWithAllFieldsAndCoverGetters() {
        // Arrange
        Long id = 1L;
        Long customerId = 123L;
        LocalDateTime orderDate = LocalDateTime.now();
        OrderStatus status = OrderStatus.PENDING;
        List<OrderItem> items = List.of(OrderItem.builder().quantity(1).price(new BigDecimal("100.00")).build());
        int version = 5;

        // Act
        Order order = Order.builder()
                .id(id)
                .customerId(customerId)
                .orderDate(orderDate)
                .status(status)
                .items(items)
                .version(version)
                .build();

        // Assert
        assertEquals(id, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(orderDate, order.getOrderDate());
        assertEquals(status, order.getStatus());
        assertEquals(items, order.getItems());
        assertEquals(version, order.getVersion());
        assertEquals(0, new BigDecimal("100.00").compareTo(order.getTotalPrice()));
    }

    @Test
    @DisplayName("Builder should calculate total price correctly from items")
    void builder_shouldCalculateTotalPriceCorrectly() {
        // Arrange
        OrderItem item1 = OrderItem.builder().quantity(2).price(new BigDecimal("10.00")).build(); // 2 * 10.00 = 20.00
        OrderItem item2 = OrderItem.builder().quantity(1).price(new BigDecimal("5.50")).build();  // 1 * 5.50 = 5.50
        List<OrderItem> items = List.of(item1, item2);

        // Act
        Order order = Order.builder()
                .items(items)
                .build();

        // Assert
        assertEquals(0, new BigDecimal("25.50").compareTo(order.getTotalPrice()));
    }

    @Test
    @DisplayName("Builder should set total price to zero when items list is null")
    void builder_shouldHandleNullItems() {
        // Act
        Order order = Order.builder()
                .items(null)
                .build();

        // Assert
        assertEquals(0, BigDecimal.ZERO.compareTo(order.getTotalPrice()));
    }

    @Test
    @DisplayName("Builder should set total price to zero when items list is empty")
    void builder_shouldHandleEmptyItems() {
        // Act
        Order order = Order.builder()
                .items(Collections.emptyList())
                .build();

        // Assert
        assertEquals(0, BigDecimal.ZERO.compareTo(order.getTotalPrice()));
    }

    @Test
    @DisplayName("process() should change status to PROCESSING when status is PENDING")
    void process_shouldChangeStatusToProcessing_whenPending() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PENDING).build();

        // Act
        order.process();

        // Assert
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"})
    @DisplayName("process() should throw IllegalStateException when status is not PENDING")
    void process_shouldThrowException_whenNotPending(OrderStatus initialStatus) {
        // Arrange
        Order order = Order.builder().status(initialStatus).build();
        String expectedMessage = String.format(ErrorMessages.CANNOT_PROCESS_ORDER, initialStatus);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::process);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("ship() should change status to SHIPPED when status is PROCESSING")
    void ship_shouldChangeStatusToShipped_whenProcessing() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.PROCESSING).build();

        // Act
        order.ship();

        // Assert
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "SHIPPED", "DELIVERED", "CANCELLED"})
    @DisplayName("ship() should throw IllegalStateException when status is not PROCESSING")
    void ship_shouldThrowException_whenNotProcessing(OrderStatus initialStatus) {
        // Arrange
        Order order = Order.builder().status(initialStatus).build();
        String expectedMessage = String.format(ErrorMessages.CANNOT_SHIP_ORDER, initialStatus);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::ship);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("deliver() should change status to DELIVERED when status is SHIPPED")
    void deliver_shouldChangeStatusToDelivered_whenShipped() {
        // Arrange
        Order order = Order.builder().status(OrderStatus.SHIPPED).build();

        // Act
        order.deliver();

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "PROCESSING", "DELIVERED", "CANCELLED"})
    @DisplayName("deliver() should throw IllegalStateException when status is not SHIPPED")
    void deliver_shouldThrowException_whenNotShipped(OrderStatus initialStatus) {
        // Arrange
        Order order = Order.builder().status(initialStatus).build();
        String expectedMessage = String.format(ErrorMessages.CANNOT_DELIVER_ORDER, initialStatus);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::deliver);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"PENDING", "PROCESSING"})
    @DisplayName("cancel() should change status to CANCELLED when status is cancellable")
    void cancel_shouldChangeStatusToCancelled_whenCancellable(OrderStatus initialStatus) {
        // Arrange
        Order order = Order.builder().status(initialStatus).build();

        // Act
        order.cancel();

        // Assert
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"SHIPPED", "DELIVERED"})
    @DisplayName("cancel() should throw IllegalStateException when status is not cancellable")
    void cancel_shouldThrowException_whenNotCancellable(OrderStatus initialStatus) {
        // Arrange
        Order order = Order.builder().status(initialStatus).build();
        String expectedMessage = ErrorMessages.CANNOT_CANCEL_ORDER;

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::cancel);
        assertEquals(expectedMessage, exception.getMessage());
    }
}