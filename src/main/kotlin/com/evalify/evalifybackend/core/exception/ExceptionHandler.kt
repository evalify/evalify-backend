package com.evalify.evalifybackend.core.exception

import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.core.DTO.ValidationErrorResponseDTO
import jakarta.servlet.http.HttpServletRequest
import java.time.Instant
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class ExceptionHandler {

    private val logger by logger()

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(
            ex: NotFoundException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Resource not found: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.NOT_FOUND.value(),
                        error = "Resource Not Found",
                        message = ex.message ?: "The requested resource was not found",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(
            ex: ValidationException,
            request: HttpServletRequest
    ): ResponseEntity<ValidationErrorResponseDTO> {
        logger.warn("Validation error: ${ex.message} for field: ${ex.field}", ex)
        val errorResponse =
                ValidationErrorResponseDTO(
                        timestamp = Instant.now().toString(),
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Validation Failed",
                        message = ex.message ?: "Validation failed",
                        field = ex.field,
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(
            ex: UnauthorizedException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Unauthorized access attempt: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = "Unauthorized",
                        message = ex.message ?: "Access denied",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(
            ex: ConflictException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Conflict occurred: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.CONFLICT.value(),
                        error = "Conflict",
                        message = ex.message ?: "Resource conflict",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(BusinessLogicException::class)
    fun handleBusinessLogicException(
            ex: BusinessLogicException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Business logic violation: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        error = "Business Logic Error",
                        message = ex.message ?: "Business rule violation",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @ExceptionHandler(ExternalServiceException::class)
    fun handleExternalServiceException(
            ex: ExternalServiceException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.error("External service error for service ${ex.service}: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.SERVICE_UNAVAILABLE.value(),
                        error = "External Service Error",
                        message = "External service is currently unavailable",
                        path = request.requestURI,
                        details = mapOf("service" to (ex.service ?: "unknown"))
                )
        return ResponseEntity(errorResponse, HttpStatus.SERVICE_UNAVAILABLE)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
            ex: MethodArgumentNotValidException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Method argument validation failed: ${ex.message}")
        val fieldErrors =
                ex.bindingResult.fieldErrors.associate {
                    it.field to (it.defaultMessage ?: "Invalid value")
                }
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Validation Failed",
                        message = "Input validation failed",
                        path = request.requestURI,
                        details = mapOf("fieldErrors" to fieldErrors)
                )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(
            ex: BindException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Binding exception: ${ex.message}")
        val fieldErrors =
                ex.bindingResult.fieldErrors.associate {
                    it.field to (it.defaultMessage ?: "Invalid value")
                }
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Binding Error",
                        message = "Request binding failed",
                        path = request.requestURI,
                        details = mapOf("fieldErrors" to fieldErrors)
                )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
            ex: MethodArgumentTypeMismatchException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Type mismatch for parameter ${ex.name}: ${ex.message}")
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Type Mismatch",
                        message = "Invalid parameter type for '${ex.name}'",
                        path = request.requestURI,
                        details =
                                mapOf(
                                        "parameter" to ex.name,
                                        "expectedType" to
                                                (ex.requiredType?.simpleName ?: "unknown"),
                                        "providedValue" to (ex.value?.toString() ?: "null")
                                )
                )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
            ex: HttpMessageNotReadableException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("HTTP message not readable: ${ex.message}")
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.BAD_REQUEST.value(),
                        error = "Malformed Request",
                        message = "Request body is malformed or missing",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
            ex: HttpRequestMethodNotSupportedException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("HTTP method not supported: ${ex.method}")
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.METHOD_NOT_ALLOWED.value(),
                        error = "Method Not Allowed",
                        message = "HTTP method '${ex.method}' is not supported for this endpoint",
                        path = request.requestURI,
                        details =
                                mapOf(
                                        "supportedMethods" to
                                                (ex.supportedMethods?.toList() ?: emptyList())
                                )
                )
        return ResponseEntity(errorResponse, HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(
            ex: DataAccessException,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.error("Database access error: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        error = "Database Error",
                        message = "An error occurred while accessing the database",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
            ex: Exception,
            request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.error("Unexpected error occurred: ${ex.message}", ex)
        val errorResponse =
                ErrorResponseDTO(
                        status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        error = "Internal Server Error",
                        message = "An unexpected error occurred",
                        path = request.requestURI
                )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
