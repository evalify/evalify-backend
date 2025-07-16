package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.Mockito.*
import java.util.*

@WebMvcTest(QuizStudentController::class)
class QuizStudentControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var previewQuizService: PreviewQuizService

    @MockBean
    private lateinit var quizStudentService: com.evalify.evalifybackend.quiz.service.QuizStudentService

    @MockBean
    private lateinit var quizCacheService: com.evalify.evalifybackend.quiz.service.QuizCacheService

    @Test
    fun `should return preview quizzes for student`() {
        // Given
        val studentId = "student123"
        `when`(previewQuizService.getPreviewQuiz(studentId)).thenReturn(emptyList())

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/quiz/preview", studentId))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray)

        verify(previewQuizService).getPreviewQuiz(studentId)
    }

    @Test
    fun `should return preview quizzes filtered by status`() {
        // Given
        val studentId = "student123"
        val status = QuizStatus.ACTIVE
        `when`(previewQuizService.getPreviewQuizByStatus(studentId, status)).thenReturn(emptyList())

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/quiz/preview", studentId)
            .param("status", status.name))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray)

        verify(previewQuizService).getPreviewQuizByStatus(studentId, status)
    }

    @Test
    fun `should return specific quiz preview`() {
        // Given
        val studentId = "student123"
        val quizId = UUID.randomUUID()
        `when`(previewQuizService.getPreviewQuizById(quizId, studentId)).thenReturn(null)

        // When & Then
        mockMvc.perform(get("/api/student/{studentId}/quiz/{quizId}/preview", studentId, quizId))
            .andExpect(status().isNotFound)

        verify(previewQuizService).getPreviewQuizById(quizId, studentId)
    }
}
