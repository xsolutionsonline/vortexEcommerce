package io.vortex.commerce.orderservice.infrastructure.out.mapper;

import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderItemPersistenceMapper;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderItemPersistenceMapperImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {OrderItemPersistenceMapperImpl.class})
class OrderItemPersistenceMapperTest {

    @Autowired
    private OrderItemPersistenceMapper orderItemPersistenceMapper;

    @Test
    @DisplayName("Debería mapear OrderItemEntity a OrderItem de dominio correctamente")
    void shouldMapEntityToDomain() {
        // Arrange
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setProductId(100L);
        entity.setQuantity(5);
        entity.setPrice(new BigDecimal("99.99"));
       
        // Act
        OrderItem domain = orderItemPersistenceMapper.toDomain(entity);

        // Assert
        assertNotNull(domain);
        assertEquals(100L, domain.getProductId());
        assertEquals(5, domain.getQuantity());
        assertEquals(new BigDecimal("99.99"), domain.getPrice());
    }

    @Test
    @DisplayName("Debería mapear OrderItem de dominio a OrderItemEntity ignorando id y order")
    void shouldMapDomainToEntity() {
        // Arrange
        OrderItem domain = OrderItem.builder()
                .productId(200L)
                .quantity(10)
                .price(new BigDecimal("12.50"))
                .build();

        // Act
        OrderItemEntity entity = orderItemPersistenceMapper.toEntity(domain);

        // Assert
        assertNotNull(entity);
        assertNull(entity.getId(), "El ID de la entidad debe ser nulo después del mapeo inicial.");
        assertNull(entity.getOrder(), "La referencia a la orden debe ser nula después del mapeo inicial.");
        assertEquals(200L, entity.getProductId());
        assertEquals(10, entity.getQuantity());
        assertEquals(new BigDecimal("12.50"), entity.getPrice());
    }
}