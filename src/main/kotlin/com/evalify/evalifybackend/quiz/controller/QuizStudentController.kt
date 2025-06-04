package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.service.QuizStudentService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("student{studentId}/quiz/{id}/")
class QuizStudentController(
    val quizStudentService: QuizStudentService,
) {

    @GetMapping
    fun getQuiz(@PathVariable studentId: UUID,@PathVariable id: UUID, request: HttpServletRequest){
        quizStudentService.getQuizQuestions(studentId = studentId, quizId = id, ipAddress = request.remoteAddr)
    }
}