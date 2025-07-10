package com.evalify.evalifybackend.batch.controller

import com.evalify.evalifybackend.batch.exception.BatchBankException
import com.evalify.evalifybackend.batch.exception.BatchDepartmentException
import com.evalify.evalifybackend.batch.exception.BatchManagerException
import com.evalify.evalifybackend.batch.exception.BatchNotFoundException
import com.evalify.evalifybackend.batch.exception.BatchSemesterException
import com.evalify.evalifybackend.batch.exception.BatchStudentException
import com.evalify.evalifybackend.batch.exception.BatchValidationException
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
@Order(-1)
class BatchExceptionController {

    private val logger by logger()

    @ExceptionHandler(BatchNotFoundException::class)
    fun handleBatchNotFound(
        ex: BatchNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch not found: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.NOT_FOUND.value(),
                error = "Batch Not Found",
                message = ex.message ?: "The requested batch was not found",
                path = request.requestURI
            )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(BatchValidationException::class)
    fun handleBatchValidation(
        ex: BatchValidationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch validation error: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Validation Error",
                message = ex.message ?: "Invalid batch data provided",
                path = request.requestURI,
                details = if (ex.field != null) mapOf("field" to ex.field) else null
            )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(BatchStudentException::class)
    fun handleBatchStudent(
        ex: BatchStudentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch student error: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.CONFLICT.value(),
                error = "Batch Student Error",
                message = ex.message ?: "Error processing student operation for batch",
                path = request.requestURI,
                details = if (ex.batchId != null) mapOf("batchId" to ex.batchId.toString()) else null
            )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(BatchSemesterException::class)
    fun handleBatchSemester(
        ex: BatchSemesterException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch semester error: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.CONFLICT.value(),
                error = "Batch Semester Error",
                message = ex.message ?: "Error processing semester operation for batch",
                path = request.requestURI,
                details = if (ex.batchId != null) mapOf("batchId" to ex.batchId.toString()) else null
            )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(BatchManagerException::class)
    fun handleBatchManager(
        ex: BatchManagerException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch manager error: {}", ex.message)
        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.CONFLICT.value(),
                error = "Batch Manager Error",
                message = ex.message ?: "Error processing manager operation for batch",
                path = request.requestURI,
                details = if (ex.batchId != null) mapOf("batchId" to ex.batchId.toString()) else null
            )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(BatchDepartmentException::class)
    fun handleBatchDepartment(
        ex: BatchDepartmentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch department error: {}", ex.message)
        val details = mutableMapOf<String, String>()
        ex.batchId?.let { details["batchId"] = it.toString() }
        ex.departmentId?.let { details["departmentId"] = it.toString() }

        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.CONFLICT.value(),
                error = "Batch Department Error",
                message = ex.message ?: "Error processing department operation for batch",
                path = request.requestURI,
                details = if (details.isNotEmpty()) details else null
            )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(BatchBankException::class)
    fun handleBatchBank(
        ex: BatchBankException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDTO> {
        logger.warn("Batch bank error: {}", ex.message)
        val details = mutableMapOf<String, String>()
        ex.batchId?.let { details["batchId"] = it.toString() }
        ex.bankId?.let { details["bankId"] = it.toString() }

        val errorResponse =
            ErrorResponseDTO(
                timestamp = Instant.now(),
                status = HttpStatus.CONFLICT.value(),
                error = "Batch Bank Error",
                message = ex.message ?: "Error processing bank operation for batch",
                path = request.requestURI,
                details = if (details.isNotEmpty()) details else null
            )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }
}