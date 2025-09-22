package io.vortex.commerce.orderservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    @DisplayName("Record should be created with correct values and accessors should work")
    void recordCreationAndAccessors() {
        // Arrange
        Long id = 1L;
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");

        // Act
        Product product = new Product(id, name, price);

        // Assert
        assertEquals(id, product.id());
        assertEquals(name, product.name());
        assertEquals(0, price.compareTo(product.price()));
    }

    @Test
    @DisplayName("Equals and HashCode should be consistent for equal records")
    void equalsAndHashCode_shouldBeConsistent() {
        // Arrange
        Product product1 = new Product(1L, "Test Product", new BigDecimal("99.99"));
        Product product2 = new Product(1L, "Test Product", new BigDecimal("99.99"));
        Product product3 = new Product(2L, "Another Product", new BigDecimal("129.99"));

        // Assert
        assertEquals(product1, product2, "Records with the same values should be equal.");
        assertNotEquals(product1, product3, "Records with different values should not be equal.");
        assertEquals(product1.hashCode(), product2.hashCode(), "Hash codes for equal records should be the same.");
    }

    @Test
    @DisplayName("toString should return a non-empty string containing field values")
    void toString_shouldReturnNonEmptyString() {
        // Arrange
        Product product = new Product(1L, "Test Product", new BigDecimal("99.99"));

        // Act & Assert
        String productString = product.toString();
        assertTrue(productString.contains("id=1"), "toString should contain the id.");
        assertTrue(productString.contains("name=Test Product"), "toString should contain the name.");
        assertTrue(productString.contains("price=99.99"), "toString should contain the price.");
    }
}