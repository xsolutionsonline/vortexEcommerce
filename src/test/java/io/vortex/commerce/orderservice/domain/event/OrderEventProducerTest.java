package io.vortex.commerce.orderservice.domain.event;

import io.vortex.commerce.orderservice.domain.model.Order;
import io.vortex.commerce.orderservice.infrastructure.adapter.out.kafka.OrderEventProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEventProducerTest {

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @InjectMocks
    private OrderEventProducer orderEventProducer;

    private final String testTopic = "order-events-test";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderEventProducer, "orderEventsTopic", testTopic);
    }

    @Test
    void sendOrderEvent_shouldSendEventSuccessfully() {
        // Arrange
        Order order = Order.builder().id(1L).build();
        OrderEvent event = new OrderEvent(null, null, OrderEvent.EventType.CREATED, order);

        ProducerRecord<String, OrderEvent> producerRecord = new ProducerRecord<>(testTopic, String.valueOf(order.getId()), event);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(testTopic, 0), 0, 0, 0, 0L, 0, 0);
        SendResult<String, OrderEvent> sendResult = new SendResult<>(producerRecord, recordMetadata);
        CompletableFuture<SendResult<String, OrderEvent>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(any(String.class), any(String.class), any(OrderEvent.class))).thenReturn(future);

        // Act
        orderEventProducer.sendOrderEvent(event);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderEvent> eventCaptor = ArgumentCaptor.forClass(OrderEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertEquals(testTopic, topicCaptor.getValue());
        assertEquals("1", keyCaptor.getValue());
        assertEquals(event, eventCaptor.getValue());
    }

    @Test
    void sendOrderEvent_shouldHandleFailure() {
        // Arrange
        Order order = Order.builder().id(2L).build();
        OrderEvent event = new OrderEvent(null, null, OrderEvent.EventType.CANCELLED, order);
        CompletableFuture<SendResult<String, OrderEvent>> future = CompletableFuture.failedFuture(new RuntimeException("Kafka is down"));
        when(kafkaTemplate.send(any(String.class), any(String.class), any(OrderEvent.class))).thenReturn(future);

        // Act
        orderEventProducer.sendOrderEvent(event);

        // Assert
        verify(kafkaTemplate).send(testTopic, "2", event);
    }
}