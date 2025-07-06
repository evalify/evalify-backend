package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/student/{studentId}/quiz/{quizId}")
class QuizStudentController(
    val quizStudentService: QuizStudentService,
) {

    @GetMapping("/start")
    fun startQuiz(@PathVariable studentId: String,@PathVariable quizId: UUID, request: HttpServletRequest)
    : List<QuizQuestionsReturnDTO?>{
        val requestTime = Instant.now()
        return quizStudentService.getQuizQuestions(studentId = studentId, quizId = quizId, ipAddress = request.remoteAddr,requestTime = requestTime)
    }

    @PatchMapping("/update")
    fun updateQuiz(@PathVariable studentId: String,@PathVariable quizId: UUID, request: HttpServletRequest){

    }

    @PutMapping("/{questionId}/save")
    fun saveQuestion(@PathVariable studentId: String,@PathVariable quizId: UUID,@PathVariable questionId: UUID, request: HttpServletRequest){

    }

    @PostMapping("/submit")
    fun submitQuiz(@PathVariable studentId: String,@PathVariable quizId: UUID, request: HttpServletRequest){

    }
}