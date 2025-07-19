package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.service.QuizCacheService
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils.getCurrentUserId
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

/**
 * Controller handling student-specific quiz operations and interactions.
 *
 * Use Cases:
 * - Starting quiz attempts for students
 * - Managing quiz state during student attempts
 * - Handling answer submissions and updates
 * - Managing quiz progress and completion
 * - Cache management for quiz responses
 * - Saving and submitting quiz answers
 *
 * This controller manages the student experience during quiz taking,
 * including caching mechanisms for reliable answer storage and submission.
 */
@RestController
@RequestMapping("/api/student/quiz/{quizId}")
class QuizStudentController(
    private val quizStudentService: QuizStudentService,
    private val quizCacheService: QuizCacheService,
    private val redisTemplate: RedisTemplate<String, QuizQuestionsReturnDTO>
) {

    /**
     * Initiates a new quiz attempt for a student.
     *
     * Use Cases:
     * - Starts a new quiz attempt for a student
     * - Retrieves cached questions if available
     * - Fetches new questions from database if not cached
     * - Stores questions in cache for future use
     *
     * @param quizId The unique identifier of the quiz
     * @param request The HTTP request containing client information (e.g., IP address)
     * @param dto Data transfer object containing quiz start parameters
     * @return ResponseEntity containing quiz questions and tags
     * @throws Exception if user is not authorized
     */
    @GetMapping("/start")
    fun startQuiz(@PathVariable quizId: UUID, request: HttpServletRequest,@RequestBody dto : StartQuizDTO)
    : ResponseEntity<QuizQuestionReturnDTO?>{
        val studentId = getCurrentUserId()?: throw Exception("You are not authorized to access this resource.")
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

    /**
     * Updates the quiz progress with student responses.
     *
     * Use Cases:
     * - Updates quiz with responses from cache if no responses provided
     * - Updates quiz with explicitly provided responses
     * - Handles bulk update of multiple question responses
     *
     * @param studentId The ID of the student taking the quiz
     * @param quizId The unique identifier of the quiz
     * @param responses Optional list of responses to update. If null, responses are fetched from cache
     */
    @PatchMapping("/update")
    fun updateQuiz(@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
        val studentId = getCurrentUserId()?: throw Exception("You are not authorized to access this resource.")

        if(responses == null)
        {
            val responses = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
            quizStudentService.updateQuiz(quizId = quizId,studentId = studentId,responses = responses)
        }
        else{
            quizStudentService.updateQuiz(quizId = quizId,studentId = studentId,responses = responses)
        }
    }

    /**
     * Updates the cached answers for a specific question in the quiz.
     *
     * Use Cases:
     * - Caches individual question responses
     * - Provides temporary storage for answers before final submission
     * - Ensures answer persistence during quiz attempt
     *
     * @param quizId The unique identifier of the quiz
     * @param answer The response to be cached
     */
    @PatchMapping("/updateCache")
    fun updateCache(@PathVariable quizId: UUID, answer: ResponseDTO){
        val studentId = getCurrentUserId()?: throw Exception("You are not authorized to access this resource.")

        quizCacheService.updateCache(quizId,studentId,answer)

    }

    /**
     * Saves a single question response to the database.
     *
     * Use Cases:
     * - Persists individual question responses
     * - Allows for progressive saving during quiz attempt
     * - Ensures answer retention in case of session interruption
     *
     * @param quizId The unique identifier of the quiz
     * @param answer The response to be saved to the database
     */
    @PatchMapping("/save")
    fun saveQuestion(@PathVariable quizId: UUID,answer:ResponseDTO){
        val studentId = getCurrentUserId()?: throw Exception("You are not authorized to access this resource.")

        quizStudentService.saveQuestion(quizId,studentId,answer)

    }

    /**
     * Submits the entire quiz for grading.
     *
     * Use Cases:
     * - Finalizes quiz attempt
     * - Submits all cached responses if no responses provided
     * - Submits explicitly provided responses
     * - Marks quiz as completed
     *
     * @param quizId The unique identifier of the quiz
     * @param responses Optional list of final responses. If null, responses are fetched from cache
     */
    @PatchMapping("/submit")
    fun submitQuiz(@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
        val studentId = getCurrentUserId()?: throw Exception("You are not authorized to access this resource.")

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