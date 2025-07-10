package com.evalify.evalifybackend.bank.controller

import com.evalify.evalifybackend.bank.exception.BankAccessDeniedException
import com.evalify.evalifybackend.bank.exception.BankNotFoundException
import com.evalify.evalifybackend.bank.exception.BankValidationException
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.core.exception.ValidationException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class BankExceptionController {

    private val logger by logger()
    // Local exception handlers for controller-specific error responses
    @ExceptionHandler(BankNotFoundException::class)
    fun handleBankNotFound(
        ex: BankNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Bank not found: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "Bank Not Found",
                message = ex.message ?: "The requested bank was not found",
                path = request.requestURI
            )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(BankAccessDeniedException::class)
    fun handleBankAccessDenied(
        ex: BankAccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Bank access denied: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.FORBIDDEN.value(),
                error = "Access Denied",
                message = ex.message ?: "You don't have permission to access this bank",
                path = request.requestURI
            )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    @ExceptionHandler(BankValidationException::class)
    fun handleBankValidation(
        ex: BankValidationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Bank validation error: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Validation Error",
                message = ex.message ?: "Invalid bank data provided",
                path = request.requestURI,
                details = if (ex.field != null) mapOf("field" to ex.field) else null
            )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
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

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Invalid argument: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Invalid Argument",
                message = ex.message ?: "Invalid request parameter",
                path = request.requestURI
            )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.error("Unexpected error in bank controller: {}", ex.message, ex)
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