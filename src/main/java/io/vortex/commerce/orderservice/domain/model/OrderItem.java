package io.vortex.commerce.orderservice.domain.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItem {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private Order order ;
}
