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

/**
 * Controller responsible for managing individual questions within question banks.
 *
 * Use Cases:
 * - Retrieving individual bank questions for viewing or editing
 * - Managing question metadata and content
 * - Handling question-specific permissions and access control
 * - Providing question details for quiz creation and bank management
 *
 * This controller handles operations specific to individual questions within banks,
 * providing endpoints for question retrieval and management.
 */
@RestController
@RequestMapping("/api/bank/questions")
class BankQuestionController(
    private val bankQuestionService: BankQuestionService
) {

    private val logger by logger()

    /**
     * Retrieves a specific question from the question bank by its ID.
     *
     * Use Cases:
     * - Viewing question details
     * - Question preview before quiz inclusion
     * - Question editing and management
     * - Access control verification
     *
     * @param questionId UUID of the question to retrieve
     * @return ResponseEntity containing the question details
     * @throws UnauthorizedException if user is not authenticated
     * @throws BankQuestionNotFoundException if question doesn't exist
     * @throws BankAccessDeniedException if user lacks access rights
     */
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
