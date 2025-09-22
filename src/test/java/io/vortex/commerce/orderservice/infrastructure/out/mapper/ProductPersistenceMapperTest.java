package io.vortex.commerce.orderservice.infrastructure.out.mapper;

import io.vortex.commerce.orderservice.domain.model.Product;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapperImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ProductPersistenceMapperImpl.class})
class ProductPersistenceMapperTest {

    @Autowired
    private ProductPersistenceMapper productPersistenceMapper;

    @Test
    @DisplayName("Debería mapear ProductEntity a Product de dominio correctamente")
    void shouldMapEntityToDomain() {
        // Arrange
        ProductEntity entity = new ProductEntity();
        entity.setId(1L);
        entity.setName("Test Product");
        entity.setPrice(new BigDecimal("199.99"));
        entity.setStock(50);
        entity.setVersion(1);

        // Act
        Product domain = productPersistenceMapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.id());
        assertEquals(entity.getName(), domain.name());
        assertEquals(0, entity.getPrice().compareTo(domain.price()));
    }

    @Test
    @DisplayName("Debería mapear Product de dominio a ProductEntity correctamente")
    void shouldMapDomainToEntity() {
        // Arrange
        Product domain = new Product(2L, "Domain Product", new BigDecimal("49.95"));

        // Act
        ProductEntity entity = productPersistenceMapper.toEntity(domain);

        // Assert
        assertNotNull(entity);
        assertEquals(domain.id(), entity.getId());
        assertEquals(domain.name(), entity.getName());
        assertEquals(0, domain.price().compareTo(entity.getPrice()));
    }
}