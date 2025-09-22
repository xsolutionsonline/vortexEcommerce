package io.vortex.commerce.orderservice.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.vortex.commerce.orderservice.domain.exception.OrderNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class OrderNotFoundExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_withMessage_shouldSetMessage() {
        // Arrange
        String errorMessage = "Order not found.";

        // Act
        OrderNotFoundException exception = new OrderNotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause(), "The cause should be null when not provided.");
    }
}