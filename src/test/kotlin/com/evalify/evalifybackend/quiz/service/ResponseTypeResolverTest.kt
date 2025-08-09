package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.responses.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest
class ResponseTypeResolverTest {

    @Autowired
    private lateinit var responseTypeResolver: ResponseTypeResolver

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test MCQ response deserialization`() {
        val questionId = UUID.randomUUID()
        val answerId = UUID.randomUUID()
        
        val body = mapOf(
            "questionId" to questionId.toString(),
            "answer" to answerId.toString(),
            "duration" to 1000L,
            "type" to "MCQ"
        )
        
        val result = objectMapper.convertValue(body, ResponseDTO::class.java)
        
        assertTrue(result is MCQResponseDTO)
        assertEquals(questionId, result.questionId)
        assertEquals(1000L, result.duration)
        assertEquals(answerId, (result as MCQResponseDTO).answer)
    }

    @Test
    fun `test MMCQ response deserialization`() {
        val questionId = UUID.randomUUID()
        val answerIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        
        val body = mapOf(
            "questionId" to questionId.toString(),
            "answer" to answerIds.map { it.toString() },
            "duration" to 2000L,
            "type" to "MMCQ"
        )
        
        val result = objectMapper.convertValue(body, ResponseDTO::class.java)
        
        assertTrue(result is MMCQResponseDTO)
        assertEquals(questionId, result.questionId)
        assertEquals(2000L, result.duration)
        assertEquals(answerIds, (result as MMCQResponseDTO).answer)
    }

    @Test
    fun `test TrueFalse response deserialization`() {
        val questionId = UUID.randomUUID()
        
        val body = mapOf(
            "questionId" to questionId.toString(),
            "answer" to true,
            "duration" to 500L,
            "type" to "TRUEFALSE"
        )
        
        val result = objectMapper.convertValue(body, ResponseDTO::class.java)
        
        assertTrue(result is TrueFalseResponseDTO)
        assertEquals(questionId, result.questionId)
        assertEquals(500L, result.duration)
        assertTrue((result as TrueFalseResponseDTO).answer == true)
    }

    @Test
    fun `test Coding response deserialization`() {
        val questionId = UUID.randomUUID()
        val code = "print('Hello World')"
        
        val body = mapOf(
            "questionId" to questionId.toString(),
            "answer" to code,
            "duration" to 5000L,
            "type" to "CODING"
        )
        
        val result = objectMapper.convertValue(body, ResponseDTO::class.java)
        
        assertTrue(result is CodingResponseDTO)
        assertEquals(questionId, result.questionId)
        assertEquals(5000L, result.duration)
        assertEquals(code, (result as CodingResponseDTO).answer)
    }

    @Test
    fun `test response type resolver with MCQ`() {
        val questionId = UUID.randomUUID()
        val answerId = UUID.randomUUID()
        
        val body = mapOf(
            "questionId" to questionId.toString(),
            "answer" to answerId.toString(),
            "duration" to 1000L
        )
        
        val result = responseTypeResolver.deserializeResponseWithType(body, QuestionTypes.MCQ)
        
        assertTrue(result is MCQResponseDTO)
        assertEquals(questionId, result.questionId)
        assertEquals(1000L, result.duration)
        assertEquals(answerId, (result as MCQResponseDTO).answer)
    }
}
