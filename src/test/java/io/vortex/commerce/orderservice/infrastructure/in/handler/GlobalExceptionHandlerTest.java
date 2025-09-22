package io.vortex.commerce.orderservice.infrastructure.in.handler;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import io.vortex.commerce.orderservice.domain.exception.ConcurrencyConflictException;
import io.vortex.commerce.orderservice.domain.exception.OrderNotFoundException;
import io.vortex.commerce.orderservice.domain.exception.ProductNotFoundException;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.dto.ErrorResponse;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.web.handler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("handleNotFoundException should return 404 for OrderNotFoundException")
    void handleNotFoundException_forOrderNotFound() {
        // Arrange
        OrderNotFoundException ex = new OrderNotFoundException("Order not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Order not found", response.getBody().message());
        assertEquals("/api/test", response.getBody().path());
    }

    @Test
    @DisplayName("handleNotFoundException should return 404 for ProductNotFoundException")
    void handleNotFoundException_forProductNotFound() {
        // Arrange
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(ex, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody().message());
    }

    @Test
    @DisplayName("handleBadRequestException should return 400 for IllegalArgumentException")
    void handleBadRequestException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    @DisplayName("handleIllegalStateException should return 409 for IllegalStateException")
    void handleIllegalStateException() {
        // Arrange
        IllegalStateException ex = new IllegalStateException("Invalid state");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalStateException(ex, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Invalid state", response.getBody().message());
    }

    @Test
    @DisplayName("handleConcurrencyConflictException should return 409")
    void handleConcurrencyConflictException() {
        // Arrange
        ConcurrencyConflictException ex = new ConcurrencyConflictException("Concurrency error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConcurrencyConflictException(ex, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Concurrency error", response.getBody().message());
    }

    @Test
    @DisplayName("handleAuthorizationDeniedException should return 403")
    void handleAuthorizationDeniedException() {
        // Arrange
        AuthorizationDeniedException ex = new AuthorizationDeniedException("Access denied", new AuthorizationDecision(false));

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleAuthorizationDeniedException(ex, request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ErrorMessages.FORBIDDEN_ERROR, response.getBody().message());
    }

    @Test
    @DisplayName("handleGenericException should return 500")
    void handleGenericException() {
        // Arrange
        Exception ex = new Exception("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(ErrorMessages.GENERIC_ERROR, response.getBody().message());
    }
}