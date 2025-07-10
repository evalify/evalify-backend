package com.evalify.evalifybackend.semester.controller

import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.semester.exception.*
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@ControllerAdvice
@Order(-1)
class SemesterExceptionController {

    @ExceptionHandler(SemesterNotFoundException::class)
    fun handleSemesterNotFoundException(
        ex: SemesterNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Semester Not Found",
            message = ex.message ?: "Semester not found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(SemesterValidationException::class)
    fun handleSemesterValidationException(
        ex: SemesterValidationException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Semester Validation Error",
            message = ex.message ?: "Semester validation failed",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ManagersNotFoundException::class)
    fun handleManagersNotFoundException(
        ex: ManagersNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Managers Not Found",
            message = ex.message ?: "Some managers could not be found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(CoursesNotFoundException::class)
    fun handleCoursesNotFoundException(
        ex: CoursesNotFoundException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.NOT_FOUND.value(),
            error = "Courses Not Found",
            message = ex.message ?: "Some courses could not be found",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(CourseAssignmentException::class)
    fun handleCourseAssignmentException(
        ex: CourseAssignmentException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Course Assignment Error",
            message = ex.message ?: "Failed to assign course to semester",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SemesterServiceException::class)
    fun handleSemesterServiceException(
        ex: SemesterServiceException,
        request: WebRequest
    ): ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(
            timestamp = Instant.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Semester Service Error",
            message = ex.message ?: "Error in semester service operation",
            path = request.getDescription(false).removePrefix("uri=")
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
