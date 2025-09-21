package io.vortex.commerce.orderservice.domain.model;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        BigDecimal price
) {}