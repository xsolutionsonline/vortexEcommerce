package io.vortex.commerce.orderservice.infrastructure.out.persistence.entity;

import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;

import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderEntityPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debería persistir y recuperar una OrderEntity con sus items correctamente")
    void shouldSaveAndRetrieveOrderWithItems() {
        // Arrange
        OrderEntity order = new OrderEntity();
        order.setCustomerId(123L);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(new BigDecimal("250.00"));

        OrderItemEntity item1 = new OrderItemEntity();
        item1.setProductId(1L);
        item1.setQuantity(2);
        item1.setPrice(new BigDecimal("100.00"));
        item1.setOrder(order);

        order.setItems(List.of(item1));

        // Act
        OrderEntity savedOrder = entityManager.persistAndFlush(order);
        entityManager.clear();
        OrderEntity foundOrder = entityManager.find(OrderEntity.class, savedOrder.getId());

        // Assert
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isNotNull();
        assertThat(foundOrder.getCustomerId()).isEqualTo(123L);
        assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(foundOrder.getItems()).hasSize(1);
        assertThat(foundOrder.getItems().get(0).getProductId()).isEqualTo(1L);
        assertThat(foundOrder.getItems().get(0).getOrder().getId()).isEqualTo(foundOrder.getId());
    }

    @Test
    @DisplayName("Debería incrementar el campo de versión en cada actualización")
    void shouldIncrementVersionOnUpdate() {
        // Arrange
        OrderEntity order = new OrderEntity();
        order.setCustomerId(456L);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        OrderEntity savedOrder = entityManager.persistAndFlush(order);
        assertThat(savedOrder.getVersion()).isEqualTo(0);

        // Act
        savedOrder.setStatus(OrderStatus.PROCESSING);
        OrderEntity updatedOrder = entityManager.persistAndFlush(savedOrder);

        // Assert
        assertThat(updatedOrder.getVersion()).isEqualTo(1);
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }
}