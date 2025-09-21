package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.repository;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.port.in.FindOrdersUseCase;
import io.vortex.commerce.orderservice.domain.port.out.OrderRepositoryPort;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.mapper.OrderPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.specification.OrderSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<Order> findByQuery(FindOrdersUseCase.FindOrdersQuery query, Pageable pageable) {
        var spec = OrderSpecifications.fromQuery(query);

        return jpaOrderRepository.findAll(spec, pageable)
                .map(mapper::toDomain);
    }
}
