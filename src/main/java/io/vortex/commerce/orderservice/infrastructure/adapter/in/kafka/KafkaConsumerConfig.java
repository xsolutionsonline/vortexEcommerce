package io.vortex.commerce.orderservice.infrastructure.adapter.in.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    private final String orderEventsTopic;
    private final int partitions;
    private final int replicas;

    public KafkaConsumerConfig(@Value("${app.kafka.topic.order-events}") String orderEventsTopic,
                               @Value("${app.kafka.topic.partitions:1}") int partitions,
                               @Value("${app.kafka.topic.replicas:1}") int replicas) {
        this.orderEventsTopic = orderEventsTopic;
        this.partitions = partitions;
        this.replicas = replicas;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory,
            KafkaTemplate<Object, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);

        var errorHandler = new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(1000L, 2L));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * Bean para crear automáticamente el topic de eventos de órdenes si no existe.
     * Esto es útil para entornos de desarrollo y pruebas.
     * En producción, los topics suelen gestionarse de forma explícita.
     */
    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(orderEventsTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}