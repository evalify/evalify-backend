package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.service.QuizCacheService
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils.getCurrentUserId
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/quiz/{quizId}")
@Transactional
class QuizStudentController(
    private val quizStudentService: QuizStudentService,
    private val quizCacheService: QuizCacheService
) {
    @PostMapping("/start")
    fun startQuiz(@PathVariable quizId: UUID, request: HttpServletRequest, @RequestBody dto: StartQuizDTO)
    : ResponseEntity<QuizQuestionReturnDTO?> {
        val requestTime = Instant.now()
        val studentId = getCurrentUserId()

        val result = quizStudentService.startQuiz(
            quizId = quizId,
            studentId = studentId,
            ipAddress = request.remoteAddr,
            requestTime = requestTime,
            password = dto.password,
            quizCacheService = quizCacheService
        )

        return ResponseEntity.ok(result)
    }

    @PatchMapping("/update")
    fun updateQuiz(@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
        val studentId = getCurrentUserId()

        if(responses == null)
        {
            val responses = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
            quizStudentService.updateQuiz(quizId = quizId,studentId = studentId,responses = responses)
        }
        else{
            quizStudentService.updateQuiz(quizId = quizId,studentId = studentId,responses = responses)
        }
    }

    @PatchMapping("/updateCache")
    fun updateCache(@PathVariable quizId: UUID, answer: ResponseDTO){
        val studentId = getCurrentUserId()
        quizCacheService.updateCache(quizId,studentId,answer)

    }

    @PatchMapping("/save")
    fun saveQuestion(@PathVariable quizId: UUID,answer:ResponseDTO){
        val studentId = getCurrentUserId()
        quizStudentService.saveQuestion(quizId,studentId,answer)

    }

    @PatchMapping("/submit")
    fun submitQuiz(@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
        val studentId = getCurrentUserId()
        if(responses == null)
        {
            val responses = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
            quizStudentService.submitQuiz(quizId = quizId,studentId = studentId,responses = responses)
        }
        else{
            quizStudentService.submitQuiz(quizId = quizId,studentId = studentId,responses = responses)
        }

    }
}