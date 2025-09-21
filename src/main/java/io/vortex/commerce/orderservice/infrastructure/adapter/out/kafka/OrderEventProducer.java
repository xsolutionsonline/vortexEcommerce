package io.vortex.commerce.orderservice.infrastructure.adapter.out.kafka;

import io.vortex.commerce.orderservice.domain.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${app.kafka.topic.order-events}")
    private String orderEventsTopic;

    public void sendOrderEvent(OrderEvent event) {
        try {
            String orderId = String.valueOf(event.getPayload().getId());
            log.info("Attempting to send event: {} for order ID: {}", event.getEventType(), orderId);

            // El método send es asíncrono y devuelve un CompletableFuture.
            // Usamos whenComplete para manejar el resultado sin bloquear el hilo principal.
            kafkaTemplate.send(orderEventsTopic, orderId, event).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully produced event for order ID: {} to offset {}",
                            orderId, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to produce event for order ID: {}", orderId, ex);
                }
            });
        } catch (Exception e) {
            // Este catch es para errores síncronos, como problemas de serialización antes de enviar.
            log.error("Synchronous error while trying to send Kafka event for order ID: {}", event.getPayload().getId(), e);
        }
    }
}