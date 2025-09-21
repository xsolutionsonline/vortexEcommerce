package io.vortex.commerce.orderservice.domain.event;

import io.vortex.commerce.orderservice.domain.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private UUID eventId = UUID.randomUUID();
    private Instant eventTimestamp = Instant.now();
    private EventType eventType;
    private Order payload;

    public enum EventType { CREATED, STATUS_UPDATED, CANCELLED }
}