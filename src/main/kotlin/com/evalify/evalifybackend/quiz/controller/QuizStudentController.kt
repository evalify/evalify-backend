package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.weaver.patterns.TypePatternQuestions
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("student{studentId}/quiz/{id}/")
class QuizStudentController(
    val quizStudentService: QuizStudentService,
) {

    @GetMapping
    fun getQuiz(@PathVariable studentId: String,@PathVariable id: UUID, request: HttpServletRequest)
    : List<QuestionsReturnDTO?>{
        val requestTime = Instant.now()
        return quizStudentService.getQuizQuestions(studentId = studentId, quizId = id, ipAddress = request.remoteAddr,requestTime = requestTime)
    }
}