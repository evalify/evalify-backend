package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.core.exception.ValidationException
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.PermutationsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.criteria.SelectionCriteriaDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.PatchQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.UpdateQuizCourseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.UpdateQuizLabDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.UpdateQuizStudentDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO
import com.evalify.evalifybackend.quiz.exception.*
import com.evalify.evalifybackend.quiz.service.QuizCourseService
import com.evalify.evalifybackend.quiz.service.QuizLabService
import com.evalify.evalifybackend.quiz.service.QuizQuestionService
import com.evalify.evalifybackend.quiz.service.QuizService
import com.evalify.evalifybackend.quiz.util.QuizValidationUtils
import com.evalify.evalifybackend.security.utils.SecurityUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import java.time.Instant
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/quiz")
class QuizController(
        private val quizService: QuizService,
        private val quizCourseService: QuizCourseService,
        private val quizLabService: QuizLabService,
        private val quizQuestionService: QuizQuestionService
) {

    private val logger by logger()

    // Local exception handlers for controller-specific error responses
    @ExceptionHandler(QuizNotFoundException::class)
    fun handleQuizNotFound(
            ex: QuizNotFoundException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Quiz not found: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.NOT_FOUND.value(),
                        error = "Quiz Not Found",
                        message = ex.message ?: "The requested quiz was not found",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(QuizAccessDeniedException::class)
    fun handleQuizAccessDenied(
            ex: QuizAccessDeniedException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Quiz access denied: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.FORBIDDEN.value(),
                        error = "Access Denied",
                        message = ex.message ?: "You don't have permission to access this quiz",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(QuizValidationException::class)
    fun handleQuizValidation(
            ex: QuizValidationException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Quiz validation error: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Validation Error",
                        message = ex.message ?: "Invalid quiz data provided",
                        path = request.requestURI,
                        details = if (ex.field != null) mapOf("field" to ex.field) else null
                )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(QuizStateException::class)
    fun handleQuizState(
            ex: QuizStateException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Quiz state error: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.CONFLICT.value(),
                        error = "Quiz State Error",
                        message = ex.message ?: "Quiz is in invalid state for this operation",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(QuizTimingException::class)
    fun handleQuizTiming(
            ex: QuizTimingException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Quiz timing error: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.CONFLICT.value(),
                        error = "Quiz Timing Error",
                        message = ex.message ?: "Quiz timing constraints violated",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(QuizDatabaseException::class)
    fun handleQuizDatabase(
            ex: QuizDatabaseException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.error("Quiz database error: {}", ex.message, ex)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        error = "Database Error",
                        message = "A database error occurred while processing your request",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(
            ex: ValidationException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Validation error: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Validation Error",
                        message = ex.message ?: "Invalid data provided",
                        path = request.requestURI,
                        details = if (ex.field != null) mapOf("field" to ex.field) else null
                )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(
            ex: UnauthorizedException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Unauthorized access: {}", ex.message)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = ex.message ?: "Authentication required",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
            ex: Exception,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.error("Unexpected error in quiz controller: {}", ex.message, ex)
        val errorResponse =
                ErrorResponseDTO(
                        timestamp = Instant.now(),
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        error = "Internal Server Error",
                        message = "An unexpected error occurred while processing your request",
                        path = request.requestURI
                )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    fun createQuiz(@Valid @RequestBody quizDTO: CreateQuizDTO): ResponseEntity<Map<String, Any>> {
        val userId = getCurrentUserId()
        logger.info("Creating quiz '{}' for user: {}", quizDTO.name, userId)

        QuizValidationUtils.validateCreateQuizData(quizDTO)
        val quizId = quizService.createQuiz(quizDTO, userId)

        logger.info("Successfully created quiz with ID: {} for user: {}", quizId, userId)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapOf("message" to "Quiz created successfully", "quizId" to quizId))
    }

    @GetMapping("/{quizId}")
    fun getQuiz(@PathVariable quizId: UUID): ResponseEntity<Any> {
        val userId = getCurrentUserId()
        logger.info("Fetching quiz: {} for user: {}", quizId, userId)

        val quiz = quizService.getQuizById(quizId, userId)

        logger.debug("Successfully retrieved quiz: {} for user: {}", quizId, userId)
        return ResponseEntity.ok(quiz)
    }

    @PatchMapping("/{quizId}")
    fun editQuiz(
            @Valid @RequestBody dto: PatchQuizDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, Any>> {
        val userId = getCurrentUserId()
        logger.info("Editing quiz: {} by user: {}", quizId, userId)

        QuizValidationUtils.validatePatchQuizData(dto)
        val updatedQuiz = quizService.editQuiz(dto, quizId, userId)

        logger.info("Successfully updated quiz: {} by user: {}", quizId, userId)
        return ResponseEntity.ok(
                mapOf("message" to "Quiz updated successfully", "quiz" to updatedQuiz)
        )
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteQuiz(@PathVariable quizId: UUID): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Deleting quiz: {} by user: {}", quizId, userId)

        quizService.deleteQuiz(quizId, userId)

        logger.info("Successfully deleted quiz: {} by user: {}", quizId, userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{quizId}/add-student")
    fun addStudent(
            @Valid @RequestBody studentDTO: UpdateQuizStudentDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        logger.info("Adding students to quiz: {} by user: {}", quizId, userId)

        quizService.addStudentToQuiz(quizId = quizId, studentId = studentDTO.studentId)

        logger.info("Successfully added students to quiz: {}", quizId)
        return ResponseEntity.ok(mapOf("message" to "Students added successfully"))
    }

    @DeleteMapping("/{quizId}/remove-student")
    fun removeStudent(
            @Valid @RequestBody studentDTO: UpdateQuizStudentDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        logger.info("Removing student from quiz: {} by user: {}", quizId, userId)

        quizService.removeStudentFromQuiz(quizId = quizId, studentId = studentDTO.studentId)

        logger.info("Successfully removed student from quiz: {}", quizId)
        return ResponseEntity.ok(mapOf("message" to "Student removed successfully"))
    }

    @PostMapping("/{quizId}/add-course")
    fun addCourseToQuiz(
            @Valid @RequestBody courseDTO: UpdateQuizCourseDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        logger.info("Adding course to quiz: {} by user: {}", quizId, userId)

        quizCourseService.assignCourseToQuiz(courseId = courseDTO.course, quizId = quizId)

        logger.info("Successfully added course to quiz: {}", quizId)
        return ResponseEntity.ok(mapOf("message" to "Course added successfully"))
    }

    @DeleteMapping("/{quizId}/remove-course")
    fun removeCourse(
            @Valid @RequestBody courseDTO: UpdateQuizCourseDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        logger.info("Removing course from quiz: {} by user: {}", quizId, userId)

        quizCourseService.removeCourseFromQuiz(courseId = courseDTO.course, quizId = quizId)

        logger.info("Successfully removed course from quiz: {}", quizId)
        return ResponseEntity.ok(mapOf("message" to "Course removed successfully"))
    }

    @PostMapping("/{quizId}/add-lab")
    fun addLabToQuiz(
            @Valid @RequestBody labDTO: UpdateQuizLabDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        logger.info("Adding lab to quiz: {} by user: {}", quizId, userId)

        quizLabService.assignLabToQuiz(labId = labDTO.lab, quizId = quizId)

        logger.info("Successfully added lab to quiz: {}", quizId)
        return ResponseEntity.ok(mapOf("message" to "Lab added successfully"))
    }

    @DeleteMapping("/{quizId}/remove-lab")
    fun removeLabFromQuiz(
            @Valid @RequestBody labDTO: UpdateQuizLabDTO,
            @PathVariable quizId: UUID
    ): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        logger.info("Removing lab from quiz: {} by user: {}", quizId, userId)

        quizLabService.removeLabToQuiz(labId = labDTO.lab, quizId = quizId)

        logger.info("Successfully removed lab from quiz: {}", quizId)
        return ResponseEntity.ok(mapOf("message" to "Lab removed successfully"))
    }

    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
                ?: throw UnauthorizedException("User not authenticated")
    }

    @PostMapping("{quizId}/share")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun shareQuiz(@PathVariable quizID: UUID, @RequestBody shareDTO: ShareQuizDTO) {

        quizService.shareQuiz(quizID, shareDTO)
    }

    @PostMapping("{quizId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun publishQuiz(
            @PathVariable quizId: UUID,
            @RequestParam(required = false) quizSets: Int,
            @RequestBody dto: SelectionCriteriaDTO
    ) {

        quizService.publishQuiz(quizId, quizSets, dto)
    }

    @GetMapping("/{quizId}/combinations")
    @ResponseStatus(HttpStatus.OK)
    fun checkAvail(
            @PathVariable quizId: UUID,
            @RequestBody dto: SelectionCriteriaDTO
    ): ResponseEntity<PermutationsDTO> {
        val result = quizService.checkAvailability(quizId, dto)
        return ResponseEntity.ok(result)
    }

    // returns a count of total, live, upcoming and completed for all quizzes
    @GetMapping("/count")
    fun getAllQuizCount(): ResponseEntity<Map<String, Long>> {
        val counts = quizService.getQuizCounts()

        logger.debug("Successfully retrieved quiz counts for admin:")
        return ResponseEntity.ok(counts)
    }

}
