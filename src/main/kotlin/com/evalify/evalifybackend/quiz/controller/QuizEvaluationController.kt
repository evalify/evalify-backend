package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.evaluation.SaveGrade
import com.evalify.evalifybackend.quiz.domain.DTO.evaluation.SaveGradeDTO
import com.evalify.evalifybackend.quiz.domain.DTO.evaluation.QuizResultDTO
import com.evalify.evalifybackend.quiz.domain.DTO.evaluation.QuizResponseDTO
import com.evalify.evalifybackend.quiz.service.QuizEvaluationService
import com.evalify.evalifybackend.quiz.service.QuizCleanupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/evaluate/quiz/{quizId}")
class QuizEvaluationController(
    private val quizEvaluationService: QuizEvaluationService,
    private val quizCleanupService: QuizCleanupService
) {

    @PostMapping("/student/{studentId}/save-grades")
    fun saveGrades(
        @PathVariable studentId: String, 
        @PathVariable quizId: UUID,
        @RequestBody gradesReq: SaveGrade
    ): ResponseEntity<String> {
        return try {

            quizEvaluationService.saveGrades(quizId, studentId, gradesReq.grades)
            ResponseEntity.ok("${gradesReq.grades.size} grade(s) saved successfully for quiz $quizId")
        } catch (e: IllegalArgumentException) {
            if (e.message?.contains("Invalid UUID string") == true || e.message?.contains("UUID") == true) {
                ResponseEntity.badRequest().body("Invalid quiz ID format: $quizId")
            } else if (e.message?.contains("cannot be transformed to Json object") == true) {
                ResponseEntity.badRequest().body("Data corruption detected. Please try again or contact support.")
            } else {
                ResponseEntity.badRequest().body("Invalid request: ${e.message}")
            }
        } catch (e: IllegalStateException) {
            when {
                e.message?.contains("not been submitted") == true -> 
                    ResponseEntity.badRequest().body("Cannot grade quiz that has not been submitted yet")
                e.message?.contains("corruption detected") == true || e.message?.contains("serialization error") == true -> 
                    ResponseEntity.badRequest().body("Data corruption detected. Corrupted data has been cleared. Please try again.")
                else -> 
                    ResponseEntity.badRequest().body("Invalid state: ${e.message}")
            }
        } catch (e: com.evalify.evalifybackend.core.exception.NotFoundException) {
            ResponseEntity.badRequest().body("Not found: ${e.message}")
        } catch (e: org.springframework.orm.jpa.JpaSystemException) {
            // Handle JPA JSON transformation errors
            if (e.message?.contains("cannot be transformed to Json object") == true || e.cause?.message?.contains("cannot be transformed to Json object") == true) {
                ResponseEntity.badRequest().body("Database serialization error detected. Corrupted data has been cleared. Please try again.")
            } else {
                ResponseEntity.internalServerError().body("Database error occurred while saving grades")
            }
        } catch (e: Exception) {
            if (e.message?.contains("cannot be transformed to Json object") == true) {
                ResponseEntity.badRequest().body("Data corruption detected. Corrupted data has been cleared. Please try again.")
            } else {
                ResponseEntity.internalServerError().body("An error occurred while saving grades: ${e.message}")
            }
        }
    }

    @PostMapping("/student/{studentId}/get-responses")
    fun getStudentResponses(
        @PathVariable studentId: String, 
        @PathVariable quizId: String
    ): ResponseEntity<Any> {
        return try {
            val quizUUID = UUID.fromString(quizId)
            // This could be implemented to return student responses for grading interface
            // For now, returning a placeholder
            ResponseEntity.ok("Feature to get student responses for grading - to be implemented")
        } catch (e: IllegalArgumentException) {
            if (e.message?.contains("Invalid UUID string") == true || e.message?.contains("UUID") == true) {
                ResponseEntity.badRequest().body("Invalid quiz ID format: $quizId")
            } else {
                ResponseEntity.badRequest().body("Invalid request: ${e.message}")
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("An error occurred: ${e.message}")
        }
    }

    @DeleteMapping("/student/{studentId}/clear-corrupted-data")
    fun clearCorruptedDataForGrading(
        @PathVariable studentId: String, 
        @PathVariable quizId: String
    ): ResponseEntity<String> {
        return try {
            val quizUUID = UUID.fromString(quizId)
            quizCleanupService.clearCorruptedResponsesOnly(quizUUID, studentId)
            ResponseEntity.ok("Corrupted grading data cleared successfully for student $studentId in quiz $quizId")
        } catch (e: IllegalArgumentException) {
            if (e.message?.contains("Invalid UUID string") == true || e.message?.contains("UUID") == true) {
                ResponseEntity.badRequest().body("Invalid quiz ID format: $quizId")
            } else {
                ResponseEntity.badRequest().body("Invalid request: ${e.message}")
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("Error clearing corrupted data: ${e.message}")
        }
    }

    @GetMapping("/student/{studentId}/results")
    fun getQuizResults(
        @PathVariable studentId: String, 
        @PathVariable quizId: UUID
    ): ResponseEntity<Any> {
        return try {
            val results = quizEvaluationService.getQuizResults(quizId, studentId)
            ResponseEntity.ok(results)
        } catch (e: IllegalStateException) {
            when {
                e.message?.contains("not yet published") == true -> 
                    ResponseEntity.badRequest().body("Quiz results are not yet published")
                else -> 
                    ResponseEntity.badRequest().body("Invalid state: ${e.message}")
            }
        } catch (e: com.evalify.evalifybackend.core.exception.NotFoundException) {
            ResponseEntity.badRequest().body("Not found: ${e.message}")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("An error occurred: ${e.message}")
        }
    }



    @GetMapping("/student/{studentId}/responses")
    fun getQuizResponses(
        @PathVariable studentId: String, 
        @PathVariable quizId: UUID
    ): ResponseEntity<Any> {
        return try {
            val responses = quizEvaluationService.getQuizResponses(quizId, studentId)
            ResponseEntity.ok(responses)
        } catch (e: com.evalify.evalifybackend.core.exception.NotFoundException) {
            ResponseEntity.badRequest().body("Not found: ${e.message}")
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body("Invalid state: ${e.message}")
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body("An error occurred: ${e.message}")
        }
    }
}