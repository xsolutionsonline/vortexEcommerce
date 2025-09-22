package io.vortex.commerce.orderservice.infrastructure.in.kafka;

import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.infrastructure.adapter.in.kafka.NotificationConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private OrderEvent orderEvent;

    @BeforeEach
    void setUp() {
        Order orderPayload = Order.builder().id(1L).build();
        orderEvent = new OrderEvent(null, null, OrderEvent.EventType.CREATED, orderPayload);
    }

    @Test
    @DisplayName("handleOrderEvent should process event without throwing an exception")
    void handleOrderEvent_shouldProcessEventWithoutException() {
        // Act & Assert
        assertDoesNotThrow(() -> notificationConsumer.handleOrderEvent(orderEvent),
                "handleOrderEvent should not throw any exceptions for a valid event.");
    }

    @Test
    @DisplayName("handleDlt should process DLT event without throwing an exception")
    void handleDlt_shouldProcessDltEventWithoutException() {
        // Arrange
        String topic = "order-events.DLT";

        // Act & Assert
        assertDoesNotThrow(() -> notificationConsumer.handleDlt(orderEvent, topic),
                "handleDlt should not throw any exceptions for a valid event.");
    }
}