package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.core.exception.ValidationException
import com.evalify.evalifybackend.quiz.exception.QuizAccessDeniedException
import com.evalify.evalifybackend.quiz.exception.QuizDatabaseException
import com.evalify.evalifybackend.quiz.exception.QuizNotFoundException
import com.evalify.evalifybackend.quiz.exception.QuizStateException
import com.evalify.evalifybackend.quiz.exception.QuizTimingException
import com.evalify.evalifybackend.quiz.exception.QuizValidationException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class QuizExceptionController {

    private val logger by logger()


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


}