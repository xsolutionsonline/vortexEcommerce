package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.port.out.OrderRepositoryPort;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaOrderRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        var orderEntity = mapper.toEntity(order);
        var savedEntity = jpaOrderRepository.save(orderEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaOrderRepository.findById(id)
                .map(mapper::toDomain);
    }
}
