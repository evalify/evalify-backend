package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizPreviewDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizUpdateDTO
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.service.QuizInstructorService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * Controller handling instructor-specific quiz operations and management.
 *
 * Use Cases:
 * - Managing course-specific quizzes
 * - Updating quiz configurations and settings
 * - Viewing and managing quizzes created by the instructor
 * - Monitoring quiz status and progress
 * - Course-specific quiz administration
 *
 * This controller provides functionality specific to instructors for managing
 * and monitoring quizzes within their courses.
 */
@RestController
@RequestMapping("/api/quiz")
class QuizInstructorController(private val quizInstructorService: QuizInstructorService) {

    /**
     * Retrieves all quizzes for a specific course
     * @param courseId The UUID of the course
     * @return List of QuizPreviewDTO objects
     */
    @GetMapping("/course/{courseId}")
    fun getQuizzesByCourseId(@PathVariable courseId: String,@RequestParam status: QuizStatus? = null): List<QuizPreviewDTO> {
        val userId = SecurityUtils.getCurrentUserId() ?: throw UnauthorizedException("You are not authorized to access this resource.")
        return quizInstructorService.getQuizzesByCourseId(courseId,status,userId)
    }

    /**
     * Updates a quiz with the provided data
     * @param quizId The UUID of the quiz to update
     * @param quizUpdateDTO The DTO containing the updated quiz data
     * @return ResponseEntity with the updated Quiz preview DTO
     */
    @PutMapping("/{quizId}")
    fun updateQuiz(
        @PathVariable quizId: UUID,
        @RequestBody quizUpdateDTO: QuizUpdateDTO
    ): ResponseEntity<QuizPreviewDTO> {
        val updatedQuiz = quizInstructorService.updateQuiz(quizId, quizUpdateDTO)
        return ResponseEntity.ok(updatedQuiz)
    }

    @GetMapping("/quiz/me")
    fun getAllQuizzesForInstructor(): List<QuizPreviewDTO> {
        val userId = SecurityUtils.getCurrentUserId() ?: throw UnauthorizedException("You are not authorized to access this resource.")
        return quizInstructorService.getAllQuizzesForInstructor(userId = userId)
    }

}
