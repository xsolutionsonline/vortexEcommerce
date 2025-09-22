package io.vortex.commerce.orderservice.domain.event;

import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import io.vortex.commerce.orderservice.domain.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderEventTest {

    @Test
    @DisplayName("No-args constructor should initialize default fields")
    void noArgsConstructor_shouldInitializeDefaultFields() {
        // Act
        OrderEvent event = new OrderEvent();

        // Assert
        assertNotNull(event.getEventId(), "Event ID should be initialized by default.");
        assertNotNull(event.getEventTimestamp(), "Event timestamp should be initialized by default.");
        assertNull(event.getEventType(), "Event type should be null initially.");
        assertNull(event.getPayload(), "Payload should be null initially.");
    }

    @Test
    @DisplayName("All-args constructor should set all fields correctly")
    void allArgsConstructor_shouldSetAllFieldsCorrectly() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();
        OrderEvent.EventType eventType = OrderEvent.EventType.CREATED;
        Order payload = Order.builder().id(1L).build();

        // Act
        OrderEvent event = new OrderEvent(eventId, timestamp, eventType, payload);

        // Assert
        assertEquals(eventId, event.getEventId());
        assertEquals(timestamp, event.getEventTimestamp());
        assertEquals(eventType, event.getEventType());
        assertEquals(payload, event.getPayload());
    }

    @Test
    @DisplayName("Setters and Getters should work correctly")
    void settersAndGetters_shouldWorkCorrectly() {
        // Arrange
        OrderEvent event = new OrderEvent();
        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();
        OrderEvent.EventType eventType = OrderEvent.EventType.STATUS_UPDATED;
        Order payload = Order.builder().id(2L).build();

        // Act
        event.setEventId(eventId);
        event.setEventTimestamp(timestamp);
        event.setEventType(eventType);
        event.setPayload(payload);

        // Assert
        assertEquals(eventId, event.getEventId());
        assertEquals(timestamp, event.getEventTimestamp());
        assertEquals(eventType, event.getEventType());
        assertEquals(payload, event.getPayload());
    }

    @Test
    @DisplayName("Equals and HashCode should be consistent for equal objects")
    void equalsAndHashCode_shouldBeConsistent() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        Instant timestamp = Instant.now();
        OrderEvent.EventType eventType = OrderEvent.EventType.CREATED;
        Order payload = Order.builder().id(1L).build();

        OrderEvent event1 = new OrderEvent(eventId, timestamp, eventType, payload);
        OrderEvent event2 = new OrderEvent(eventId, timestamp, eventType, payload);
        OrderEvent event3 = new OrderEvent(UUID.randomUUID(), timestamp, eventType, payload); // Different ID

        // Assert
        assertEquals(event1, event2, "Objects with same field values should be equal.");
        assertNotEquals(event1, event3, "Objects with different field values should not be equal.");
        assertEquals(event1.hashCode(), event2.hashCode(), "Hash codes for equal objects should be the same.");
    }

    @Test
    @DisplayName("toString should return a non-empty string containing field values")
    void toString_shouldReturnNonEmptyString() {
        // Arrange
        OrderEvent event = new OrderEvent(UUID.randomUUID(), Instant.now(), OrderEvent.EventType.CANCELLED, Order.builder().build());

        // Act & Assert
        assertFalse(event.toString().isEmpty());
        assertTrue(event.toString().contains("eventType=CANCELLED"));
    }
}
