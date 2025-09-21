package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository;

import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import java.util.Optional;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    @Override
    @EntityGraph(value = "Order.items")
    Optional<OrderEntity> findById(Long id);

    @Override
    @EntityGraph(value = "Order.items")
    Page<OrderEntity> findAll(Specification<OrderEntity> spec, Pageable pageable);
}
