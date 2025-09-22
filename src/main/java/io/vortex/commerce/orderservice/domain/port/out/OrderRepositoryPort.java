package io.vortex.commerce.orderservice.domain.port.out;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.port.in.FindOrdersUseCase;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Page<Order> findByQuery(FindOrdersUseCase.FindOrdersQuery query, Pageable pageable);
}
