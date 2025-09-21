package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository;

import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
}
