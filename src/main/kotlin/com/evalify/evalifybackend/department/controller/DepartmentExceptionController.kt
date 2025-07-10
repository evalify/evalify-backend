package com.evalify.evalifybackend.department.exception

import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@ControllerAdvice
@Order(-1)
class DepartmentExceptionController {

    @ExceptionHandler(DepartmentNotFoundException::class)
    fun handleDepartmentNotFoundException(
        ex: DepartmentNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Department Not Found",
            message = ex.message ?: "Department not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(DepartmentAlreadyExistsException::class)
    fun handleDepartmentAlreadyExistsException(
        ex: DepartmentAlreadyExistsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Department Already Exists",
            message = ex.message ?: "Department already exists",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(DepartmentValidationException::class)
    fun handleDepartmentValidationException(
        ex: DepartmentValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Department Validation Error",
            message = ex.message ?: "Department validation failed",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DepartmentServiceException::class)
    fun handleDepartmentServiceException(
        ex: DepartmentServiceException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Department Service Error",
            message = ex.message ?: "Error in department service operation",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(DepartmentDeleteException::class)
    fun handleDepartmentDeleteException(
        ex: DepartmentDeleteException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Department Delete Error",
            message = ex.message ?: "Cannot delete department",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }
}
