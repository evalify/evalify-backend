package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.quiz.domain.DTO.student.PreviewQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.student.QuizStatusDTO
import com.evalify.evalifybackend.quiz.service.PreviewQuizStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/student/quiz")
class PreviewQuizStudentController(
    private val previewQuizStudentService: PreviewQuizStudentService
) {
    @GetMapping
    fun previewQuiz(@RequestParam quizStatus: QuizStatusDTO?):List<PreviewQuizDTO>?{
        val studentId = getCurrentUserId()
        return previewQuizStudentService.getStudentQuiz(studentId, quizStatus = quizStatus)
    }

    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
            ?: throw UnauthorizedException("User not authenticated")
    }
}