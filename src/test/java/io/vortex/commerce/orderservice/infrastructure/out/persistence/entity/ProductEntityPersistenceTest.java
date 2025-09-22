package io.vortex.commerce.orderservice.infrastructure.out.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.ProductEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductEntityPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debería persistir y recuperar una ProductEntity correctamente")
    void shouldSaveAndRetrieveProduct() {
        // Arrange
        ProductEntity product = new ProductEntity();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);

        // Act
        entityManager.persistAndFlush(product);
        entityManager.clear();
        ProductEntity foundProduct = entityManager.find(ProductEntity.class, product.getId());

        // Assert
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Test Product");
        assertThat(foundProduct.getStock()).isEqualTo(100);
        assertThat(foundProduct.getPrice()).isEqualByComparingTo("99.99");
    }

    @Test
    @DisplayName("Debería incrementar el campo de versión en cada actualización")
    void shouldIncrementVersionOnUpdate() {
        // Arrange
        ProductEntity product = new ProductEntity();
        product.setName("Versioned Product");
        ProductEntity savedProduct = entityManager.persistAndFlush(product);
        assertThat(savedProduct.getVersion()).isEqualTo(0);

        // Act
        savedProduct.setStock(90);
        ProductEntity updatedProduct = entityManager.persistAndFlush(savedProduct);

        // Assert
        assertThat(updatedProduct.getVersion()).isEqualTo(1);
        assertThat(updatedProduct.getStock()).isEqualTo(90);
    }
}