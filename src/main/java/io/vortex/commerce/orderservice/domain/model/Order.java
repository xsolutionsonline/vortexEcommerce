package io.vortex.commerce.orderservice.domain.model;

import io.vortex.commerce.orderservice.domain.constants.ErrorMessages;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Order {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private List<OrderItem> items;
    private BigDecimal totalPrice;
    private int version;

    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void process() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(String.format(ErrorMessages.CANNOT_PROCESS_ORDER, this.status));
        }
        this.status = OrderStatus.PROCESSING;
    }

    public void ship() {
        if (this.status != OrderStatus.PROCESSING) {
            throw new IllegalStateException(String.format(ErrorMessages.CANNOT_SHIP_ORDER, this.status));
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException(String.format(ErrorMessages.CANNOT_DELIVER_ORDER, this.status));
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException(ErrorMessages.CANNOT_CANCEL_ORDER);
        }
        this.status = OrderStatus.CANCELLED;
    }

}