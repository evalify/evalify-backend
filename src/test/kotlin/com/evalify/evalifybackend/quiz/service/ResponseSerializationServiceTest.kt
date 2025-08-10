package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.quiz.domain.DTO.responses.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@SpringBootTest
class ResponseSerializationServiceTest {

    @Autowired
    private lateinit var responseSerializationService: ResponseSerializationService

    @Test
    fun `test polymorphic serialization and deserialization of responses`() {
        // Create sample responses of different types
        val responses = mutableListOf<ResponseDTO>(
            MCQResponseDTO(
                questionId = UUID.randomUUID(),
                answer = UUID.randomUUID(),
                duration = 5000L
            ),
            TrueFalseResponseDTO(
                questionId = UUID.randomUUID(),
                answer = true,
                duration = 3000L
            ),
            DescriptiveResponseDTO(
                questionId = UUID.randomUUID(),
                answer = "This is my answer",
                duration = 10000L
            ),
            CodingResponseDTO(
                questionId = UUID.randomUUID(),
                answer = "public class Test { }",
                duration = 15000L
            ),
            MMCQResponseDTO(
                questionId = UUID.randomUUID(),
                answer = listOf(UUID.randomUUID(), UUID.randomUUID()),
                duration = 7000L
            )
        )

        // Serialize to JSON
        val json = responseSerializationService.serializeResponses(responses)
        assertNotNull(json)
        println("Serialized JSON: $json")

        // Deserialize back to objects
        val deserializedResponses = responseSerializationService.deserializeResponses(json)
        
        // Verify the results
        assertEquals(responses.size, deserializedResponses.size)
        
        // Check each response maintains its type and data
        responses.forEachIndexed { index, originalResponse ->
            val deserializedResponse = deserializedResponses[index]
            
            assertEquals(originalResponse.questionId, deserializedResponse.questionId)
            assertEquals(originalResponse.duration, deserializedResponse.duration)
            assertEquals(originalResponse::class, deserializedResponse::class)
            
            // Type-specific checks
            when (originalResponse) {
                is MCQResponseDTO -> {
                    val deserialized = deserializedResponse as MCQResponseDTO
                    assertEquals(originalResponse.answer, deserialized.answer)
                }
                is TrueFalseResponseDTO -> {
                    val deserialized = deserializedResponse as TrueFalseResponseDTO
                    assertEquals(originalResponse.answer, deserialized.answer)
                }
                is DescriptiveResponseDTO -> {
                    val deserialized = deserializedResponse as DescriptiveResponseDTO
                    assertEquals(originalResponse.answer, deserialized.answer)
                }
                is CodingResponseDTO -> {
                    val deserialized = deserializedResponse as CodingResponseDTO
                    assertEquals(originalResponse.answer, deserialized.answer)
                }
                is MMCQResponseDTO -> {
                    val deserialized = deserializedResponse as MMCQResponseDTO
                    assertEquals(originalResponse.answer, deserialized.answer)
                }
            }
        }
    }

    @Test
    fun `test empty responses handling`() {
        val emptyResponses = mutableListOf<ResponseDTO>()
        
        val json = responseSerializationService.serializeResponses(emptyResponses)
        assertNull(json)
        
        val deserializedResponses = responseSerializationService.deserializeResponses(null)
        assertTrue(deserializedResponses.isEmpty())
    }
}
