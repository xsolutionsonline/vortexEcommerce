package io.vortex.commerce.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderItemTest {

    @Test
    @DisplayName("No-args constructor should create an empty object")
    void noArgsConstructor_shouldCreateEmptyObject() {
        // Act
        OrderItem item = new OrderItem();

        // Assert
        assertNull(item.getId());
        assertNull(item.getProductId());
        assertNull(item.getQuantity());
        assertNull(item.getPrice());
        assertNull(item.getOrder());
    }

    @Test
    @DisplayName("All-args constructor should set all fields correctly")
    void allArgsConstructor_shouldSetAllFields() {
        // Arrange
        Long id = 1L;
        Long productId = 101L;
        Integer quantity = 5;
        BigDecimal price = new BigDecimal("19.99");
        Order order = Order.builder().id(99L).build();

        // Act
        OrderItem item = new OrderItem(id, productId, quantity, price, order);

        // Assert
        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(price, item.getPrice());
        assertEquals(order, item.getOrder());
    }

    @Test
    @DisplayName("Builder should create an object with all fields set")
    void builder_shouldCreateObjectWithAllFields() {
        // Arrange
        Long id = 2L;
        Long productId = 102L;
        Integer quantity = 10;
        BigDecimal price = new BigDecimal("25.50");
        Order order = Order.builder().id(100L).build();

        // Act
        OrderItem item = OrderItem.builder()
                .id(id)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .order(order)
                .build();

        // Assert
        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(price, item.getPrice());
        assertEquals(order, item.getOrder());
    }

    @Test
    @DisplayName("Setters and Getters should work correctly")
    void settersAndGetters_shouldWorkCorrectly() {
        // Arrange
        OrderItem item = new OrderItem();
        Long id = 3L;
        Long productId = 103L;
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("9.75");
        Order order = Order.builder().id(101L).build();

        // Act
        item.setId(id);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setPrice(price);
        item.setOrder(order);

        // Assert
        assertEquals(id, item.getId());
        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(price, item.getPrice());
        assertEquals(order, item.getOrder());
    }
}