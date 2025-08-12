package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.CodingResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.DescriptiveResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.FileUploadResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.FillUpResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MMCQResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.MatchResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.TrueFalseResponseDTO
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.quiz.service.QuizCacheService
import com.evalify.evalifybackend.quiz.service.QuizCleanupService
import com.evalify.evalifybackend.quiz.service.QuizStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils.getCurrentUserId
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val quizCacheService: QuizCacheService,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val questionRepository: QuestionRepository,
    private val objectMapper : ObjectMapper,
    private val quizCleanupService: QuizCleanupService
) {
    @PostMapping("/start")
    fun startQuiz(@PathVariable quizId: UUID, request: HttpServletRequest, @RequestBody dto: StartQuizDTO)
            : ResponseEntity<QuizQuestionReturnDTO?> {
        val requestTime = Instant.now()
        val studentId = getCurrentUserId()

        return try {
            // Proactively clear any corrupted data before starting
            try {
                quizCleanupService.clearCorruptedResponsesOnly(quizId, studentId)
            } catch (e: Exception) {
                println("Pre-cleanup failed: ${e.message}")
            }

            val result = quizStudentService.startQuiz(
                quizId = quizId,
                studentId = studentId,
                ipAddress = request.remoteAddr,
                requestTime = requestTime,
                password = dto.password,
                quizCacheService = quizCacheService
            )

            ResponseEntity.ok(result)
        } catch (e: org.springframework.orm.jpa.JpaSystemException) {
            // Handle JPA JSON transformation errors
            if (e.message?.contains("cannot be transformed to Json object") == true || e.cause?.message?.contains("cannot be transformed to Json object") == true) {
                println("Database serialization error in startQuiz, clearing all data: ${e.message}")
                // Clear all corrupted data using separate service
                try {
                    quizCacheService.clearStudentCache(quizId, studentId)
                    quizCleanupService.deleteQuizStudentRecord(quizId, studentId)
                } catch (cleanupError: Exception) {
                    println("Cleanup failed: ${cleanupError.message}")
                }
                throw IllegalStateException("Database serialization error detected. Corrupted data has been cleared. Please try starting the quiz again.", e)
            }
            throw e
        } catch (e: IllegalArgumentException) {
            if (e.message?.contains("cannot be transformed to Json object") == true) {
                println("JSON transformation error in startQuiz, clearing all data: ${e.message}")
                // Clear all corrupted data using separate service
                try {
                    quizCacheService.clearStudentCache(quizId, studentId)
                    quizCleanupService.deleteQuizStudentRecord(quizId, studentId)
                } catch (cleanupError: Exception) {
                    println("Cleanup failed: ${cleanupError.message}")
                }
                throw IllegalStateException("JSON transformation error detected. Corrupted data has been cleared. Please try starting the quiz again.", e)
            }
            throw e
        } catch (e: Exception) {
            if (e.message?.contains("cannot be transformed to Json object") == true) {
                println("Serialization error in startQuiz, clearing all data: ${e.message}")
                // Clear all corrupted data using separate service
                try {
                    quizCacheService.clearStudentCache(quizId, studentId)
                    quizCleanupService.deleteQuizStudentRecord(quizId, studentId)
                } catch (cleanupError: Exception) {
                    println("Cleanup failed: ${cleanupError.message}")
                }
                throw IllegalStateException("Data corruption detected. Corrupted data has been cleared. Please try starting the quiz again.", e)
            }
            throw e
        }
    }

    fun objectMapper(body : Map<String,Any>):ResponseDTO{
        val questionId = UUID.fromString(body["questionId"] as String)
        val question = questionRepository.findById(questionId)
            .orElseThrow { IllegalArgumentException("Question not found") }
        val type = question.getQuestionType()
        val answer = when (type) {
            QuestionTypes.MCQ -> objectMapper.convertValue(body, MCQResponseDTO::class.java)
            QuestionTypes.CODING -> objectMapper.convertValue(body, CodingResponseDTO::class.java)
            QuestionTypes.MMCQ -> objectMapper.convertValue(body, MMCQResponseDTO::class.java)
            QuestionTypes.TRUEFALSE -> objectMapper.convertValue(body, TrueFalseResponseDTO::class.java)
            QuestionTypes.DESCRIPTIVE -> objectMapper.convertValue(body, DescriptiveResponseDTO::class.java)
            QuestionTypes.FILL_UP -> objectMapper.convertValue(body, FillUpResponseDTO::class.java)
            QuestionTypes.MATCH_THE_FOLLOWING -> objectMapper.convertValue(body, MatchResponseDTO::class.java)
            QuestionTypes.FILE_UPLOAD -> objectMapper.convertValue(body, FileUploadResponseDTO::class.java)
            else -> throw IllegalArgumentException("Unknown question type")
        }
        return answer
    }

    @PatchMapping("/update")
    fun updateQuiz(@PathVariable quizId: UUID, @RequestParam(required = true) save: Boolean? = false, @RequestBody responses: List<Map<String,Any>>? = null): ResponseEntity<String> {
        val studentId = getCurrentUserId()
        
        // Proactively clear any corrupted data before proceeding
        try {
            // Only clear question cache, preserve answer cache
            //quizCacheService.clearQuestionCacheOnly(quizId, studentId)
            quizStudentService.clearCorruptedQuizStudentData(quizId, studentId)
        } catch (e: Exception) {
            // Ignore cleanup errors and proceed
            println("Pre-cleanup failed, continuing anyway: ${e.message}")
        }
        
        if(save == false ){
            if(responses.isNullOrEmpty()){
                throw IllegalArgumentException("Responses cannot be empty or null!")
            }
            else{
                val finalResponses = responses.map { body ->
                    objectMapper(body)
                }
                finalResponses.forEach { response ->
                    updateCache(quizId,response,studentId)
                }
                return ResponseEntity.ok("Responses cached successfully")
            }
        }
        else{
            if(responses == null)
            {
                try {
                    val responsesFromCache = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
                    quizStudentService.updateQuiz(quizId = quizId,studentId = studentId,responses = responsesFromCache)
                    return ResponseEntity.ok("Cached responses saved successfully to database")
                } catch (e: org.springframework.orm.jpa.JpaSystemException) {
                    // Handle JPA JSON transformation errors specifically
                    if (e.message?.contains("cannot be transformed to Json object") == true || e.cause?.message?.contains("cannot be transformed to Json object") == true) {
                        // Clear corrupted cache and database state
                        quizCacheService.clearStudentCache(quizId, studentId)
                        quizStudentService.clearCorruptedQuizStudentData(quizId, studentId)
                        throw IllegalStateException("Data serialization error detected. Corrupted data has been cleared. Please restart the quiz and try again.", e)
                    }
                    throw e
                } catch (e: Exception) {
                    // If there's a JSON transformation error, clear the cache and throw a meaningful error
                    if (e.message?.contains("cannot be transformed to Json object") == true) {
                        quizCacheService.clearStudentCache(quizId, studentId)
                        throw IllegalStateException("Cache data is corrupted. Please restart the quiz and try again.", e)
                    }
                    throw e
                }
            }
            else{
                try {
                    val saveResponses = responses.map { body ->
                        objectMapper(body)
                    }
                    saveResponses.forEach { response ->
                        updateCache(quizId,response,studentId)
                    }
                    val result = quizStudentService.mapResponsesByQuestionId(saveResponses)
                    quizStudentService.updateQuiz(quizId,studentId,result)
                    return ResponseEntity.ok("Quiz responses saved successfully to database")
                } catch (e: org.springframework.orm.jpa.JpaSystemException) {
                    // Handle JPA JSON transformation errors specifically
                    if (e.message?.contains("cannot be transformed to Json object") == true || e.cause?.message?.contains("cannot be transformed to Json object") == true) {
                        // Clear corrupted cache and database state
                        quizCacheService.clearStudentCache(quizId, studentId)
                        quizStudentService.clearCorruptedQuizStudentData(quizId, studentId)
                        throw IllegalStateException("Database serialization error detected. Corrupted data has been cleared. Please try again.", e)
                    }
                    throw e
                } catch (e: IllegalArgumentException) {
                    if (e.message?.contains("cannot be transformed to Json object") == true) {
                        // Clear corrupted cache and database state
                        quizCacheService.clearStudentCache(quizId, studentId)
                        quizStudentService.clearCorruptedQuizStudentData(quizId, studentId)
                        throw IllegalStateException("JSON transformation error detected. Corrupted data has been cleared. Please try again.", e)
                    }
                    throw e
                } catch (e: Exception) {
                    // If there's any JSON transformation error, clear the cache and throw a meaningful error
                    if (e.message?.contains("cannot be transformed to Json object") == true) {
                        quizCacheService.clearStudentCache(quizId, studentId)
                        quizStudentService.clearCorruptedQuizStudentData(quizId, studentId)
                        throw IllegalStateException("Data corruption detected. Corrupted data has been cleared. Please try again.", e)
                    }
                    throw e
                }
            }
        }}






    fun updateCache( quizId: UUID,  body: ResponseDTO , studentId: String?){

        quizCacheService.updateCache(quizId,studentId,body)

    }

    @DeleteMapping("/clear-corrupted-data")
    fun clearCorruptedData(@PathVariable quizId: UUID): ResponseEntity<String> {
        val studentId = getCurrentUserId()
        try {
            // Clear cache first
            quizCacheService.clearStudentCache(quizId, studentId)
            // Clear database with nuclear option
            quizStudentService.clearCorruptedDataInNewTransaction(quizId, studentId)
            return ResponseEntity.ok("Corrupted data cleared successfully. You can now restart the quiz.")
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Error clearing corrupted data: ${e.message}")
        }
    }

    @DeleteMapping("/nuclear-reset")
    fun nuclearReset(@PathVariable quizId: UUID): ResponseEntity<String> {
        val studentId = getCurrentUserId()
        try {
            // Nuclear option: clear everything and force recreate
            quizCacheService.clearAllQuizCache(quizId)
            quizStudentService.clearCorruptedDataInNewTransaction(quizId, studentId)
            return ResponseEntity.ok("Quiz state completely reset. All cache and database data cleared.")
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("Error during nuclear reset: ${e.message}")
        }
    }}



//    @PatchMapping("/submit")
//    fun submitQuiz(@PathVariable quizId: UUID, responses : List<ResponseDTO>? = null){
//        val studentId = getCurrentUserId()
//        if(responses == null)
//        {
//            val responses = quizCacheService.getAllAnswers(quizId = quizId,studentId = studentId)
//            quizStudentService.submitQuiz(quizId = quizId,studentId = studentId,responses = responses)
//        }
//        else{
//            quizStudentService.submitQuiz(quizId = quizId,studentId = studentId,responses = responses)
//        }
//
//    }
