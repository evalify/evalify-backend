package com.evalify.evalifybackend.bank.controller

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankDetailsDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.CreateBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.EditBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.CreateQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.CreateTopicDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.bank.exception.BankAccessDeniedException
import com.evalify.evalifybackend.bank.exception.BankNotFoundException
import com.evalify.evalifybackend.bank.exception.BankValidationException
import com.evalify.evalifybackend.bank.service.BankManagerService
import com.evalify.evalifybackend.bank.service.BankService
import com.evalify.evalifybackend.bank.service.BankTopicService
import com.evalify.evalifybackend.bank.util.BankValidationUtils
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.DTO.ErrorResponseDTO
import com.evalify.evalifybackend.core.DTO.PageResponse
import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.core.exception.ValidationException
import com.evalify.evalifybackend.core.util.PaginationUtils
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.GetSharedUsersDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.security.utils.SecurityUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.Instant
import java.util.UUID
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bank")
class BankController(
        private val bankManagerService: BankManagerService,
        private val bankService: BankService,
        private val bankTopicService: BankTopicService
) {

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

    @GetMapping("")
    fun getDetailsOfBank(
            @RequestParam(defaultValue = "0") @Min(0) page: Int,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) size: Int,
            @RequestParam(defaultValue = "name") sortBy: String,
            @RequestParam(defaultValue = "ASC") sortDirection: String,
            @RequestParam(required = false) search: String?
    ): ResponseEntity<PageResponse<BankDetailsDTO>> {
        val userId = getCurrentUserId()
        logger.info(
                "Fetching paginated bank details for user: {} - page: {}, size: {}, sortBy: {}, search: {}",
                userId,
                page,
                size,
                sortBy,
                search
        )

        // Validate sort direction
        val sortDir =
                try {
                    Sort.Direction.valueOf(sortDirection.uppercase())
                } catch (e: IllegalArgumentException) {
                    logger.warn("Invalid sort direction '{}', defaulting to ASC", sortDirection)
                    Sort.Direction.ASC
                }

        // Validate sort field
        val validSortFields = setOf("name", "semester", "courseCode", "createdAt")
        val validatedSortBy =
                if (validSortFields.contains(sortBy)) {
                    sortBy
                } else {
                    logger.warn("Invalid sort field '{}', defaulting to 'name'", sortBy)
                    "name"
                }

        val pageRequest = PageRequest.of(page, size, Sort.by(sortDir, validatedSortBy))
        val banksPage = bankManagerService.getDetailsOfBank(userId, pageRequest, search)
        val result = PaginationUtils.toPageResponse(banksPage)

        logger.debug(
                "Retrieved {} banks from {} total for user: {}",
                result.content.size,
                result.totalElements,
                userId
        )
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{bankId}")
    fun getBankDetails(@PathVariable bankId: UUID): ResponseEntity<BankDetailsDTO> {
        val userId = getCurrentUserId()
        logger.info("Fetching bank details for bankId: {} by user: {}", bankId, userId)
        val result = bankManagerService.getBankInfo(bankId, userId)
        logger.debug("Retrieved bank details for: {}", bankId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("")
    fun createBank(@Valid @RequestBody bank: CreateBankDTO): ResponseEntity<CreateBankDTO> {
        val userId = getCurrentUserId()
        logger.info("Creating new bank for user: {}", userId)

        BankValidationUtils.validateBankCreateData(bank)
        val result = bankService.createBank(bank, userId)

        logger.info("Successfully created bank: {} for user: {}", result.name, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }
    @PatchMapping("/{bankId}")
    fun editBank(
            @Valid @RequestBody bank: EditBankDTO,
            @PathVariable bankId: UUID
    ): ResponseEntity<CreateBankDTO> {
        val userId = getCurrentUserId()
        logger.info("Editing bank: {} by user: {}", bankId, userId)

        BankValidationUtils.validateCreateData(bank)
        val updatedBank = bankService.editBank(bank, userId, bankId)

        logger.info("Successfully updated bank: {} by user: {}", bankId, userId)
        return ResponseEntity.ok(updatedBank)
    }

    @DeleteMapping("/{bankId}")
    fun deleteBank(@PathVariable bankId: UUID): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Deleting bank: {} by user: {}", bankId, userId)

        bankService.deleteBank(bankId, userId)

        logger.info("Successfully deleted bank: {} by user: {}", bankId, userId)
        return ResponseEntity.noContent().build()
    }



    @GetMapping("/{bankId}/share")
    fun getSharedBanks(@PathVariable bankId: UUID): ResponseEntity<GetSharedUsersDTO> {
        val userId = getCurrentUserId()
        logger.info("Fetching shared users for bank: {} by user: {}", bankId, userId)

        val result = bankService.getShareBank(bankId, userId)

        logger.debug("Retrieved {} shared users for bank: {}", result.sharedUsers.size, bankId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/{bankId}/share")
    fun shareBank(
            @PathVariable bankId: UUID,
            @Valid @RequestBody dto: ShareQuizDTO
    ): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Sharing bank: {} by user: {} with users: {}", bankId, userId, dto.userID)

        bankService.shareBank(bankId, dto, userId)

        logger.info("Successfully shared bank: {} with {} users", bankId, dto.userID.size)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{bankId}/share")
    fun unshareBank(
            @PathVariable bankId: UUID,
            @Valid @RequestBody dto: ShareQuizDTO
    ): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Unsharing bank: {} by user: {} from users: {}", bankId, userId, dto.userID)

        bankService.unshareBank(bankId, dto, userId)

        logger.info("Successfully unshared bank: {} from {} users", bankId, dto.userID.size)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{bankId}/topics")
    fun getBankTopics(@PathVariable bankId: UUID): ResponseEntity<List<ReturnTopicDTO>> {
        val userId = getCurrentUserId()
        logger.info("Fetching topics for bank: {} by user: {}", bankId, userId)

        val result = bankManagerService.getBankTopics(bankId, userId)

        logger.debug("Retrieved {} topics for bank: {}", result.size, bankId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/{bankId}/topic")
    fun addTopic(
            @PathVariable bankId: UUID,
            @Valid @RequestBody topic: CreateTopicDTO
    ): ResponseEntity<CreateTopicDTO> {
        val userId = getCurrentUserId()
        logger.info("Adding topic to bank: {} by user: {}", bankId, userId)

        BankValidationUtils.validateTopicData(topic)
        val result = bankTopicService.addTopic(topic, bankId, userId)

        logger.info("Successfully added topic: {} to bank: {}", result.name, bankId)
        return ResponseEntity.status(HttpStatus.CREATED).body(result)
    }

    @PatchMapping("/{bankId}/topic/{topicId}")
    fun editTopic(
            @PathVariable bankId: UUID,
            @PathVariable topicId: UUID,
            @Valid @RequestBody topic: CreateTopicDTO
    ): ResponseEntity<CreateTopicDTO> {
        val userId = getCurrentUserId()
        logger.info("Editing topic: {} in bank: {} by user: {}", topicId, bankId, userId)

        BankValidationUtils.validateTopicData(topic)
        val result = bankTopicService.editTopic(bankId, topicId, topic, userId)

        logger.info("Successfully updated topic: {} in bank: {}", topicId, bankId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{bankId}/topic/{topicId}")
    fun removeTopic(@PathVariable bankId: UUID, @PathVariable topicId: UUID): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Removing topic: {} from bank: {} by user: {}", topicId, bankId, userId)

        bankTopicService.removeTopic(bankId, topicId, userId)

        logger.info("Successfully removed topic: {} from bank: {}", topicId, bankId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{bankId}/questions")
    @Transactional
    fun getBankQuestions(@PathVariable bankId: UUID): ResponseEntity<List<BankQuestionsReturnDTO>> {
        val userId = getCurrentUserId()
        logger.info("Fetching questions for bank: {} by user: {}", bankId, userId)

        val result = bankManagerService.getBankQuestions(bankId, userId)

        logger.debug("Retrieved {} questions for bank: {}", result.size, bankId)
        return ResponseEntity.ok(result)
    }

    @Transactional
    @GetMapping("/{bankId}/questions/by-topic")
    fun getBankQuestionsByTopic(
            @PathVariable bankId: UUID,
            @RequestParam topicIds: List<UUID>
    ): ResponseEntity<List<BankQuestionsReturnDTO>> {
        val userId = getCurrentUserId()
        logger.info("Fetching questions by topics for bank: {} by user: {}", bankId, userId)

        val result = bankManagerService.getQuestionsByTopic(topicIds, bankId, userId)

        logger.debug("Retrieved {} questions by topics for bank: {}", result.size, bankId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/{bankId}/questions")
    fun addBankQuestion(
            @PathVariable bankId: UUID,
            @Valid @RequestBody bankQuestion: CreateQuestionDTO
    ): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Adding question to bank: {} by user: {}", bankId, userId)

        BankValidationUtils.validateQuestionData(bankQuestion)
        bankManagerService.createBankQuestion(bankQuestion, bankId, userId)

        logger.info("Successfully added question to bank: {}", bankId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PatchMapping("/{bankId}/questions/{questionId}")
    fun editBankQuestion(
            @PathVariable bankId: UUID,
            @PathVariable questionId: UUID,
            @Valid @RequestBody bankQuestion: PatchQuestionDTO
    ): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Editing question: {} in bank: {} by user: {}", questionId, bankId, userId)

        BankValidationUtils.validateEditQuestionData(bankQuestion)
        bankManagerService.editBankQuestion(bankQuestion, questionId, userId, bankId)

        logger.info("Successfully updated question: {} in bank: {}", questionId, bankId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{bankId}/questions/{questionId}")
    fun deleteBankQuestion(
            @PathVariable bankId: UUID,
            @PathVariable questionId: UUID
    ): ResponseEntity<Void> {
        val userId = getCurrentUserId()
        logger.info("Deleting question: {} from bank: {} by user: {}", questionId, bankId, userId)

        bankManagerService.deleteBankQuestion(questionId, bankId, userId)

        logger.info("Successfully deleted question: {} from bank: {}", questionId, bankId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/count")
    fun getBankCount(): ResponseEntity<Map<String,Long>> {
        val count = bankManagerService.getBankCount()
        logger.info("Total banks for admin: {}", count)

        return ResponseEntity.ok(mapOf("count" to count))
    }


    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
                ?: throw UnauthorizedException("User not authenticated")
    }
}
