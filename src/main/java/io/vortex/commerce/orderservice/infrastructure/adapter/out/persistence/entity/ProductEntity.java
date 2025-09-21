package io.vortex.commerce.orderservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductEntity {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    private int stock;

    @Version
    private int version;
}