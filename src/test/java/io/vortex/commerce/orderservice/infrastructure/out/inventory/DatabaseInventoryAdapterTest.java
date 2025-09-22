package io.vortex.commerce.orderservice.infrastructure.out.inventory;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import io.vortex.commerce.orderservice.domain.exception.ConcurrencyConflictException;
import io.vortex.commerce.orderservice.domain.exception.ProductNotFoundException;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.inventory.DatabaseInventoryAdapter;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository.JpaProductRepository;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.product.ProductCacheAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseInventoryAdapterTest {

    @Mock
    private JpaProductRepository productRepository;

    @Mock
    private ProductCacheAdapter productCacheAdapter;

    @InjectMocks
    private DatabaseInventoryAdapter databaseInventoryAdapter;

    private OrderItem orderItem;
    private ProductEntity product;

    @BeforeEach
    void setUp() {
        orderItem = OrderItem.builder().productId(1L).quantity(2).price(BigDecimal.TEN).build();

        product = new ProductEntity();
        product.setId(1L);
        product.setName("Test Product");
        product.setStock(10);
        product.setVersion(0);
    }

    @Test
    @DisplayName("reserveStock should decrease stock when sufficient stock is available")
    void reserveStock_shouldDecreaseStock_whenSufficientStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        databaseInventoryAdapter.reserveStock(List.of(orderItem));

        // Assert
        assertEquals(8, product.getStock());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("reserveStock should throw ProductNotFoundException when a product is not found")
    void reserveStock_shouldThrowProductNotFoundException_whenProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> databaseInventoryAdapter.reserveStock(List.of(orderItem)));

        assertEquals(String.format(ErrorMessages.PRODUCT_NOT_FOUND, 1L), exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("reserveStock should throw IllegalStateException when stock is insufficient")
    void reserveStock_shouldThrowIllegalStateException_whenInsufficientStock() {
        // Arrange
        product.setStock(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> databaseInventoryAdapter.reserveStock(List.of(orderItem)));

        assertEquals(String.format(ErrorMessages.INSUFFICIENT_STOCK_FOR_PRODUCT, 1L), exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("reserveStock should throw ConcurrencyConflictException on optimistic locking failure")
    void reserveStock_shouldThrowConcurrencyConflictException_onLockingFailure() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenThrow(new ObjectOptimisticLockingFailureException("Concurrency error", null));

        // Act & Assert
        ConcurrencyConflictException exception = assertThrows(ConcurrencyConflictException.class,
                () -> databaseInventoryAdapter.reserveStock(List.of(orderItem)));

        assertEquals(ErrorMessages.CONCURRENCY_ERROR, exception.getMessage());
    }

    @Test
    @DisplayName("reserveStock should do nothing for an empty list of items")
    void reserveStock_shouldDoNothing_forEmptyList() {
        // Act
        databaseInventoryAdapter.reserveStock(Collections.emptyList());

        // Assert
        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("releaseStock should increase stock")
    void releaseStock_shouldIncreaseStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        databaseInventoryAdapter.releaseStock(List.of(orderItem));

        // Assert
        assertEquals(12, product.getStock());
        verify(productRepository).save(product);
    }
}