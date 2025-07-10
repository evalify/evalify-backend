package com.evalify.evalifybackend.lab.controller

import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.lab.exception.LabNotFoundException
import com.evalify.evalifybackend.lab.exception.LabAlreadyExistsException
import com.evalify.evalifybackend.lab.exception.LabValidationException
import com.evalify.evalifybackend.lab.exception.LabServiceException
import com.evalify.evalifybackend.lab.exception.LabDeleteException
import com.evalify.evalifybackend.lab.exception.InvalidLabFieldException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@ControllerAdvice
@Order(-1)
class LabExceptionController {

    @ExceptionHandler(LabNotFoundException::class)
    fun handleLabNotFoundException(
        ex: LabNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Lab Not Found",
            message = ex.message ?: "Lab not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(LabAlreadyExistsException::class)
    fun handleLabAlreadyExistsException(
        ex: LabAlreadyExistsException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Lab Already Exists",
            message = ex.message ?: "Lab already exists",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(LabValidationException::class)
    fun handleLabValidationException(
        ex: LabValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Lab Validation Error",
            message = ex.message ?: "Lab validation failed",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(LabServiceException::class)
    fun handleLabServiceException(
        ex: LabServiceException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Lab Service Error",
            message = ex.message ?: "Error in lab service operation",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(LabDeleteException::class)
    fun handleLabDeleteException(
        ex: LabDeleteException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.CONFLICT.value(),
            error = "Lab Delete Error",
            message = ex.message ?: "Cannot delete lab",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(InvalidLabFieldException::class)
    fun handleInvalidLabFieldException(
        ex: InvalidLabFieldException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Invalid Lab Field",
            message = ex.message ?: "Invalid field value",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
