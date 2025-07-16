package com.evalify.evalifybackend.bank.controller

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.exception.BankAccessDeniedException
import com.evalify.evalifybackend.bank.exception.BankQuestionNotFoundException
import com.evalify.evalifybackend.bank.service.BankQuestionService
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.security.utils.SecurityUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/bank/questions")
class BankQuestionController(
    private val bankQuestionService: BankQuestionService
) {

    private val logger by logger()

    @GetMapping("/{questionId}")
    fun getBankQuestionById(@PathVariable questionId: UUID): ResponseEntity<BankQuestionsReturnDTO> {
        val userId = getCurrentUserId()
        logger.info("Fetching bank question with ID: {} by user: {}", questionId, userId)
        
        val result = bankQuestionService.getBankQuestionById(questionId, userId)
        
        logger.debug("Retrieved bank question details for ID: {}", questionId)
        return ResponseEntity.ok(result)
    }

    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
            ?: throw UnauthorizedException("User not authenticated")
    }
}
