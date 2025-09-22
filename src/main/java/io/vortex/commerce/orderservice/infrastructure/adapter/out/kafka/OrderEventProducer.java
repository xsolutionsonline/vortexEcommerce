package io.vortex.commerce.orderservice.infrastructure.adapter.out.kafka;

import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final String orderEventsTopic;

    public OrderEventProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate,
                              @Value("${app.kafka.topic.order-events}") String orderEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderEventsTopic = orderEventsTopic;
    }

    public void sendOrderEvent(OrderEvent event) {
        if (event == null || event.getPayload() == null) {
            log.warn("Attempted to send a null event or an event with a null payload.");
            return;
        }

        String orderId = "unknown";
        try {
            orderId = String.valueOf(event.getPayload().getId());
            final String finalOrderId = orderId;
            log.info("Attempting to send event: {} for order ID: {}", event.getEventType(), finalOrderId);

            // El método send es asíncrono y devuelve un CompletableFuture.
            // Usamos whenComplete para manejar el resultado sin bloquear el hilo principal.
            kafkaTemplate.send(orderEventsTopic, finalOrderId, event).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully produced event for order ID: {} to offset {}",
                            finalOrderId, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to produce event for order ID: {}", finalOrderId, ex);
                }
            });
        } catch (Exception e) {
            // Este catch es para errores síncronos, como problemas de serialización antes de enviar.
            log.error("Synchronous error while trying to send Kafka event for order ID: {}", orderId, e);
        }
    }
}