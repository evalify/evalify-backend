package com.evalify.evalifybackend.config

import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import java.time.Duration
import java.util.UUID

@Configuration
@EnableCaching
class CacheConfig {

    /**
     * Default CacheManager for @Cacheable, @CachePut, etc.
     */
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        val config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build()
    }

    /**
     * Custom RedisTemplate for storing QuizQuestionsReturnDTO as values in lists
     */
    @Bean
    fun quizQuestionRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, QuizQuestionsReturnDTO> {
        val template = RedisTemplate<String, QuizQuestionsReturnDTO>()
        template.setConnectionFactory(connectionFactory)

        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()

        template.afterPropertiesSet()
        return template
    }

    /**
     * RedisTemplate to store quiz responses in a hash structure:
     * Key = UUID (quizId/studentId), HashKey = String (questionId), Value = ResponseDTO
     */
    @Bean
    fun quizResponseRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<UUID, ResponseDTO> {
        val template = RedisTemplate<UUID, ResponseDTO>()
        template.setConnectionFactory(connectionFactory)

        template.keySerializer = GenericJackson2JsonRedisSerializer()  // UUID as JSON
        template.hashKeySerializer = StringRedisSerializer()           // questionId as string
        template.valueSerializer = GenericJackson2JsonRedisSerializer() // ResponseDTO
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()

        template.afterPropertiesSet()
        return template
    }
}
