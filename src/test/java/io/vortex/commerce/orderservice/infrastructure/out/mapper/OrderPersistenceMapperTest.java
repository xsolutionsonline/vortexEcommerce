package io.vortex.commerce.orderservice.infrastructure.out.mapper;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderItem;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderItemPersistenceMapperImpl;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapperImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest(classes = {OrderPersistenceMapperImpl.class, OrderItemPersistenceMapperImpl.class})
class OrderPersistenceMapperTest {

    @Autowired
    private OrderPersistenceMapper orderPersistenceMapper;

    @Test
    @DisplayName("Debería establecer la relación bidireccional después de mapear a entidad")
    void shouldEstablishBidirectionalRelationship_whenMappingToEntity() {
        // Arrange
        OrderItem orderItem1 = OrderItem.builder()
                .productId(1L)
                .quantity(2)
                .price(new BigDecimal("10.00"))
                .build();

        Order order = Order.builder()
                .id(1L)
                .items(List.of(orderItem1))
                .build();

        // Act
        OrderEntity orderEntity = orderPersistenceMapper.toEntity(order);

        // Assert
        assertNotNull(orderEntity.getItems());
        
        orderEntity.getItems().forEach(itemEntity -> assertSame(orderEntity, itemEntity.getOrder(), "Cada OrderItemEntity debe tener una referencia a su OrderEntity padre."));
    }
}