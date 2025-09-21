package io.vortex.commerce.orderservice.domain.port.in;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface FindOrdersUseCase {
    record FindOrdersQuery(Long customerId, OrderStatus status, LocalDate dateFrom, LocalDate dateTo) {}

    Page<Order> findOrders(FindOrdersQuery query, Pageable pageable);
}