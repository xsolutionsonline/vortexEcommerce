package io.vortex.commerce.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderStatusTest {

    @Test
    @DisplayName("Enum should contain all expected status values")
    void enumShouldContainAllExpectedValues() {
        assertNotNull(OrderStatus.valueOf("PENDING"));
        assertNotNull(OrderStatus.valueOf("PROCESSING"));
        assertNotNull(OrderStatus.valueOf("SHIPPED"));
        assertNotNull(OrderStatus.valueOf("DELIVERED"));
        assertNotNull(OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    @DisplayName("Enum should have the correct number of values")
    void enumShouldHaveCorrectNumberOfValues() {
        assertEquals(5, OrderStatus.values().length);
    }

    @Test
    @DisplayName("Enum values should have correct string representation")
    void enumValuesShouldHaveCorrectStringRepresentation() {
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("PROCESSING", OrderStatus.PROCESSING.name());
        assertEquals("SHIPPED", OrderStatus.SHIPPED.name());
        assertEquals("DELIVERED", OrderStatus.DELIVERED.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }
}