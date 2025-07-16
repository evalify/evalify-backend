package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.service.QuizCacheService
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import jakarta.servlet.http.HttpServletRequest
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
import org.springframework.data.redis.core.RedisTemplate
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/student/{studentId}/quiz/{quizId}")
class QuizStudentController(
    private val quizStudentService: QuizStudentService,
    private val quizCacheService: QuizCacheService,
    private val redisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>
) {

    @GetMapping("/start")
    fun startQuiz(@PathVariable studentId: String,@PathVariable quizId: UUID, request: HttpServletRequest,@RequestBody dto : StartQuizDTO)
    : ResponseEntity<QuizQuestionReturnDTO?>{
        val requestTime = Instant.now()
        val key = "quiz:$quizId:student:$studentId:questions"

        val cachedQuestions = redisTemplate.opsForList().range(key, 0, -1)

        if (!cachedQuestions.isNullOrEmpty()) {
            val questionsList = cachedQuestions.filterNotNull()
            return ResponseEntity.ok(QuizQuestionReturnDTO(
                questions = questionsList,
                quizTags = quizStudentService.getQuizTags(quizId)
            ))
        }

        // If not in cache, get from database
        val questions = quizStudentService.getQuizQuestions(
            studentId = studentId,
            quizId = quizId,
            ipAddress = request.remoteAddr,
            requestTime = requestTime,
            dto = dto
        )

        // Store in cache for future use
        val finalQuestions = questions?.questions ?: emptyList()
        quizCacheService.storeStudentQuestions(quizId,studentId,finalQuestions)

        return ResponseEntity.ok(questions)
    }

    @PatchMapping("/update")
    fun updateQuiz(@PathVariable studentId: String,@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
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
    fun updateCache(@PathVariable studentId: String,@PathVariable quizId: UUID, answer: ResponseDTO){
        quizCacheService.updateCache(quizId,studentId,answer)

    }

    @PatchMapping("/save")
    fun saveQuestion(@PathVariable studentId: String,@PathVariable quizId: UUID,answer:ResponseDTO){
        quizStudentService.saveQuestion(quizId,studentId,answer)

    }

    @PatchMapping("/submit")
    fun submitQuiz(@PathVariable studentId: String,@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
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