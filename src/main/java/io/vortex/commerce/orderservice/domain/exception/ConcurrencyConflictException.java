package io.vortex.commerce.orderservice.domain.exception;

public class ConcurrencyConflictException extends RuntimeException {
    public ConcurrencyConflictException(String message) {
        super(message);
    }

    public ConcurrencyConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}