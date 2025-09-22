package io.vortex.commerce.orderservice.domain.exceptions;

import io.vortex.commerce.orderservice.domain.exception.ConcurrencyConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyConflictExceptionTest {

    @Test
    @DisplayName("Constructor with message should set message correctly")
    void constructor_withMessage_shouldSetMessage() {
        // Arrange
        String errorMessage = "A concurrency conflict occurred.";

        // Act
        ConcurrencyConflictException exception = new ConcurrencyConflictException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor with message and cause should set both correctly")
    void constructor_withMessageAndCause_shouldSetBoth() {
        // Arrange
        String errorMessage = "A concurrency conflict occurred.";
        Throwable cause = new RuntimeException("Original database exception");

        // Act
        ConcurrencyConflictException exception = new ConcurrencyConflictException(errorMessage, cause);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}