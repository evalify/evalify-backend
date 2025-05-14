package com.evalify.evalifybackend.core.exception

import com.evalify.evalifybackend.core.DTO.NotFoundExceptionDTO
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<Any> {
        return ResponseEntity(NotFoundExceptionDTO(message = ex.message.toString(), statusCode = 404), HttpStatus.NOT_FOUND)
    }

}