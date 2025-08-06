package com.evalify.evalifybackend.core.config

import com.evalify.evalifybackend.core.jackson.KotlinDurationDeserializer
import com.evalify.evalifybackend.core.jackson.KotlinTimeModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration

@Configuration
class JacksonConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        val module = SimpleModule().apply {
            addDeserializer(Duration::class.java, KotlinDurationDeserializer())
        }

        return ObjectMapper()
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .registerModule(module)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

}