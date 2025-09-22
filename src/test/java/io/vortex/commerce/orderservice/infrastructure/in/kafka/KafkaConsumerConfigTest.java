package io.vortex.commerce.orderservice.infrastructure.in.kafka;

import io.vortex.commerce.orderservice.infrastructure.adapter.in.kafka.KafkaConsumerConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerConfigTest {

    @Mock
    private ConcurrentKafkaListenerContainerFactoryConfigurer configurer;

    @Mock
    private ConsumerFactory<Object, Object> kafkaConsumerFactory;

    @Mock
    private KafkaTemplate<Object, Object> kafkaTemplate;

    private KafkaConsumerConfig kafkaConsumerConfig;

    private final String testTopic = "test-order-events";
    private final int partitions = 1;
    private final int replicas = 1;

    @BeforeEach
    void setUp() {
        kafkaConsumerConfig = new KafkaConsumerConfig(testTopic, partitions, replicas);
    }

    @Test
    @DisplayName("kafkaListenerContainerFactory debe configurar la factoría con un DefaultErrorHandler")
    void kafkaListenerContainerFactory_shouldConfigureFactoryWithErrorHandler() {
        // Act
        ConcurrentKafkaListenerContainerFactory<?, ?> factory = kafkaConsumerConfig.kafkaListenerContainerFactory(
                configurer, kafkaConsumerFactory, kafkaTemplate);

        // Assert
        assertNotNull(factory);
        verify(configurer).configure(any(ConcurrentKafkaListenerContainerFactory.class), any(ConsumerFactory.class));

        Object errorHandler = ReflectionTestUtils.getField(factory, "commonErrorHandler");
        assertNotNull(errorHandler);
        assertTrue(errorHandler instanceof DefaultErrorHandler, "El manejador de errores debe ser un DefaultErrorHandler.");
    }

    @Test
    @DisplayName("orderEventsTopic bean debe crear un NewTopic con la configuración correcta")
    void orderEventsTopic_shouldCreateTopicWithCorrectConfig() {
        // Act
        NewTopic newTopic = kafkaConsumerConfig.orderEventsTopic();

        // Assert
        assertNotNull(newTopic);
        assertEquals(testTopic, newTopic.name());
        assertEquals(partitions, newTopic.numPartitions());
        assertEquals((short) replicas, newTopic.replicationFactor());
    }
}