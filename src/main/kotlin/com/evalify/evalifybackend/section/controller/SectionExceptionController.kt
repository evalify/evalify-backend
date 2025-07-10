package com.evalify.evalifybackend.section.controller

import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.section.exception.*
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@ControllerAdvice
@Order(-1)
class SectionExceptionController {

    @ExceptionHandler(SectionNotFoundException::class)
    fun handleSectionNotFoundException(
        ex: SectionNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Section Not Found",
            message = ex.message ?: "Section not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(QuizNotFoundException::class)
    fun handleQuizNotFoundException(
        ex: QuizNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Quiz Not Found",
            message = ex.message ?: "Quiz not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SectionValidationException::class)
    fun handleSectionValidationException(
        ex: SectionValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Section Validation Error",
            message = ex.message ?: "Section validation failed",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(QuestionMoveException::class)
    fun handleQuestionMoveException(
        ex: QuestionMoveException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Question Move Error",
            message = ex.message ?: "Failed to move questions between sections",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SectionServiceException::class)
    fun handleSectionServiceException(
        ex: SectionServiceException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Section Service Error",
            message = ex.message ?: "Error in section service operation",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
