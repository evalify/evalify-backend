package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizStudentsManagerDTO
import com.evalify.evalifybackend.quiz.service.QuizStudentsManagerService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/quiz/{quizId}/students/status")
class QuizStudentsManagerController(
    val quizManagerService: QuizStudentsManagerService
)
{
        @GetMapping
    fun getQuizStudents(@PathVariable quizId: UUID): ResponseEntity<QuizStudentsManagerDTO> {
        return ResponseEntity.ok(quizManagerService.getQuizStudents(quizId))
    }
    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
            ?: throw UnauthorizedException("User not authenticated")
    }
}