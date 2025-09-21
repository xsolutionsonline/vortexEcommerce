package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.specification;

import io.vortex.commerce.orderservice.domain.model.OrderStatus;
import io.vortex.commerce.orderservice.domain.port.in.FindOrdersUseCase;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class OrderSpecifications {

    public static Specification<OrderEntity> fromQuery(FindOrdersUseCase.FindOrdersQuery query) {
        Specification<OrderEntity> spec = Specification.where(null);

        if (query.customerId() != null) {
            spec = spec.and(hasCustomerId(query.customerId()));
        }
        if (query.status() != null) {
            spec = spec.and(hasStatus(query.status()));
        }
        if (query.dateFrom() != null) {
            spec = spec.and(isAfter(query.dateFrom()));
        }
        if (query.dateTo() != null) {
            spec = spec.and(isBefore(query.dateTo()));
        }

        return spec;
    }

    private static Specification<OrderEntity> hasCustomerId(Long customerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("customerId"), customerId);
    }

    private static Specification<OrderEntity> hasStatus(OrderStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<OrderEntity> isAfter(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), date.atStartOfDay());
    }

    private static Specification<OrderEntity> isBefore(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), date.atTime(23, 59, 59));
    }
}