package io.vortex.commerce.orderservice.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * Configura el CacheManager de Spring para usar Redis.
     * Define la configuración por defecto y configuraciones específicas por cache.
     *
     * @param factory la fábrica de conexiones de Redis autoconfigurada por Spring Boot.
     * @return el gestor de cache configurado.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        RedisCacheConfiguration productsConfig = defaultConfig.entryTtl(Duration.ofMinutes(30));

        return RedisCacheManagerBuilder.fromConnectionFactory(factory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("products", productsConfig)
                .build();
    }
}