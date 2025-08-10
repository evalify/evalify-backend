package com.evalify.evalifybackend.quiz.converter

import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.core.jackson.KotlinDurationDeserializer
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kotlin.time.Duration

@Converter(autoApply = false)
class ResponseDTOConverter : AttributeConverter<MutableList<ResponseDTO>, String> {

    companion object {
        private val objectMapper: ObjectMapper by lazy {
            val module = SimpleModule().apply {
                addDeserializer(Duration::class.java, KotlinDurationDeserializer())
            }

            ObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
                .registerModule(module)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    override fun convertToDatabaseColumn(attribute: MutableList<ResponseDTO>?): String? {
        if (attribute == null || attribute.isEmpty()) {
            return null
        }
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            throw RuntimeException("Error converting ResponseDTO list to JSON", e)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): MutableList<ResponseDTO>? {
        if (dbData.isNullOrBlank()) {
            return mutableListOf()
        }
        return try {
            objectMapper.readValue(dbData, object : TypeReference<MutableList<ResponseDTO>>() {})
        } catch (e: Exception) {
            throw RuntimeException("Error converting JSON to ResponseDTO list", e)
        }
    }
}
