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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderItemEntityPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debería persistir y recuperar una OrderItemEntity correctamente")
    void shouldSaveAndRetrieveOrderItem() {
        // Arrange
        OrderEntity order = new OrderEntity();
        order.setCustomerId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        OrderEntity persistedOrder = entityManager.persist(order);

        OrderItemEntity item = new OrderItemEntity();
        item.setProductId(101L);
        item.setQuantity(5);
        item.setPrice(new BigDecimal("19.99"));
        item.setOrder(persistedOrder);

        // Act
        OrderItemEntity savedItem = entityManager.persistAndFlush(item);
        entityManager.clear();
        OrderItemEntity foundItem = entityManager.find(OrderItemEntity.class, savedItem.getId());

        // Assert
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isNotNull();
        assertThat(foundItem.getProductId()).isEqualTo(101L);
        assertThat(foundItem.getQuantity()).isEqualTo(5);
        assertThat(foundItem.getPrice()).isEqualByComparingTo("19.99");
        assertThat(foundItem.getOrder()).isNotNull();
        assertThat(foundItem.getOrder().getId()).isEqualTo(persistedOrder.getId());
    }
}