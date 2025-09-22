package io.vortex.commerce.orderservice.infrastructure.adapter.in.kafka;

import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @KafkaListener(topics = "${app.kafka.topic.order-events}")
    public void handleOrderEvent(OrderEvent event) {
        log.info("Received event: {} for order ID: {}. Sending notification...", event.getEventType(), event.getPayload().getId());
        log.info("Notification sent successfully for order ID: {}", event.getPayload().getId());
    }

    @DltHandler
    public void handleDlt(OrderEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Event {} from topic {} received in DLT. Payload: {}", event.getEventType(), topic, event.getPayload());
    }
}