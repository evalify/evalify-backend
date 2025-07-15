package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.DTO.AccessDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankDetailsDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.CreateQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.bank.exception.BankNotFoundException
import com.evalify.evalifybackend.bank.exception.BankQuestionNotFoundException
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.bank.util.BankSecurityUtils
import com.evalify.evalifybackend.bank.util.BankValidationUtils
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.exception.BusinessLogicException
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedUserDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SimpleUserDTO
import com.evalify.evalifybackend.quiz.question.domain.CodingQuestion
import com.evalify.evalifybackend.quiz.question.domain.DescriptiveQuestion
import com.evalify.evalifybackend.quiz.question.domain.FileUpload
import com.evalify.evalifybackend.quiz.question.domain.FillUp.FillUp
import com.evalify.evalifybackend.quiz.question.domain.FillUp.blanks
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQ
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MMCQ
import com.evalify.evalifybackend.quiz.question.domain.MCQ.TrueFalse
import com.evalify.evalifybackend.quiz.question.domain.MatchPair
import com.evalify.evalifybackend.quiz.question.domain.MatchTheFollowing
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.quiz.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID


@Service
@Transactional
class BankManagerService(
        private val bankRepository: BankRepository,
        private val userRepository: UserRepository,
        private val topicRepo: TopicRepo,
        private val baseQuestionRepository: QuestionRepository,
        private val bankQuestionRepository: BankQuestionRepository
) {

        private val logger by logger()

        fun getDetailsOfBank(
                userId: String,
                pageable: org.springframework.data.domain.Pageable,
                searchTerm: String? = null
        ): org.springframework.data.domain.Page<BankDetailsDTO> {
                logger.info(
                        "Fetching paginated bank details for user: {} with page: {}, size: {}",
                        userId,
                        pageable.pageNumber,
                        pageable.pageSize
                )

                val banksPage =
                        if (searchTerm.isNullOrBlank()) {
                                bankRepository.findBanksByUserAccess(userId, pageable)
                        } else {
                                logger.debug("Searching banks with term: {}", searchTerm)
                                bankRepository.findBanksByUserAccessAndNameContaining(
                                        userId,
                                        searchTerm,
                                        pageable
                                )
                        }

                val result =
                        banksPage.map { bank ->
                                BankDetailsDTO(
                                        id = bank.id,
                                        courseCode = bank.courseCode,
                                        name = bank.name,
                                        semester = "S${bank.semester}",
                                        questions = bank.bankQuestion.size,
                                        topics = bank.topics?.size,
                                        access =
                                                bank.sharedUsers.map { bankUser ->
                                                        // Create SimpleUserDTO to avoid
                                                        // serialization issues with Hibernate
                                                        // proxies
                                                        val simpleUser =
                                                                bankUser.user?.let { user ->
                                                                        // Force initialization if
                                                                        // needed
                                                                        val actualUser =
                                                                                if (user.id != null
                                                                                ) {
                                                                                        userRepository
                                                                                                .findById(
                                                                                                        user.id!!
                                                                                                )
                                                                                                .orElse(
                                                                                                        null
                                                                                                )
                                                                                } else null

                                                                        actualUser?.let {
                                                                                SimpleUserDTO(
                                                                                        id =
                                                                                                it.id!!,
                                                                                        name =
                                                                                                it.name,
                                                                                        email =
                                                                                                it.email,
                                                                                        profileId =
                                                                                                it.profileId
                                                                                )
                                                                        }
                                                                }
                                                        SharedUserDTO(simpleUser, bankUser.tags)
                                                }
                                )
                        }

                logger.debug(
                        "Retrieved {} banks from {} total for user: {}",
                        result.content.size,
                        result.totalElements,
                        userId
                )
                return result
        }
                // or it.id, or build DTO


        fun getBankInfo(bankId: UUID, userId: String): BankDetailsDTO {
                logger.info("Fetching bank info for bankId: {} by user: {}", bankId, userId)

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureBankAccess(bank, userId)
                val result =
                        BankDetailsDTO(
                                id = bank.id,
                                courseCode = bank.courseCode,
                                name = bank.name,
                                semester = "S${bank.semester}",
                                questions = bank.bankQuestion.size,
                                topics = bank.topics?.size,
                                access =
                                        bank.sharedUsers.map { bankUser ->
                                                // Create SimpleUserDTO to avoid serialization
                                                // issues with Hibernate proxies
                                                val simpleUser =
                                                        bankUser.user?.let { user ->
                                                                // Force initialization if needed
                                                                val actualUser =
                                                                        if (user.id != null) {
                                                                                userRepository
                                                                                        .findById(
                                                                                                user.id!!
                                                                                        )
                                                                                        .orElse(
                                                                                                null
                                                                                        )
                                                                        } else null

                                                                actualUser?.let {
                                                                        SimpleUserDTO(
                                                                                id = it.id!!,
                                                                                name = it.name,
                                                                                email = it.email,
                                                                                profileId =
                                                                                        it.profileId
                                                                        )
                                                                }
                                                        }
                                                SharedUserDTO(simpleUser, bankUser.tags)
                                        }
                        )

                logger.debug("Retrieved bank info for bankId: {}", bankId)
                return result
        }

        fun getBankQuestions(bankId: UUID, userId: String): List<BankQuestionsReturnDTO> {
                logger.info("Fetching bank questions for bankId: {} by user: {}", bankId, userId)

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureBankAccess(bank, userId)

                val bankQuestions = bank.bankQuestion
                val result =
                        bankQuestions.map { bankQuestion ->
                                val baseQuestion = bankQuestion.question
                                baseQuestion.mapToBankType(bankQuestion.id)
                        }

                logger.debug("Retrieved {} questions for bank: {}", result.size, bankId)
                return result
        }

        fun getBankTopics(bankId: UUID, userId: String): List<ReturnTopicDTO> {
                logger.info("Fetching bank topics for bankId: {} by user: {}", bankId, userId)

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureBankAccess(bank, userId)

                val topics = bank.topics
                val result =
                        topics?.map { topic -> ReturnTopicDTO(id = topic.id, name = topic.name) }
                                ?: emptyList()

                logger.debug("Retrieved {} topics for bank: {}", result.size, bankId)
                return result
        }


        fun getQuestionsByTopic(
                topicIds: List<UUID>?,
                bankId: UUID,
                userId: String
        ): List<BankQuestionsReturnDTO> {
                logger.info(
                        "Fetching questions by topics for bankId: {} by user: {}",
                        bankId,
                        userId
                )

                val bank = bankRepository.findById(bankId).orElseThrow {
                        BankNotFoundException(bankId.toString())
                }
                BankSecurityUtils.ensureBankAccess(bank, userId)

                val result = if (topicIds.isNullOrEmpty()) {
                        bank.bankQuestion.filter { bankQuestion ->
                                bankQuestion.question.topic.isEmpty()
                        }
                } else {
                        val topics = topicRepo.findAllById(topicIds)
                        bank.bankQuestion.filter { bankQuestion ->
                                bankQuestion.question.topic.any { it in topics }
                        }
                }
                val finalResult = result.map { it.question.mapToBankType(it.id) }

                logger.debug("Retrieved {} questions by topics for bank: {}", finalResult.size, bankId)
                return finalResult
        }



        fun createQuestion(dto: CreateQuestionDTO, bankId: UUID): BaseQuestion {
                logger.debug("Creating question of type: {} for bank: {}", dto.type, bankId)

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }
                val topics =if (!dto.topicIds.isNullOrEmpty())
                {
                        topicRepo.findAllById(dto.topicIds)

                }
                else {
                        emptyList()
                }


                val baseQuestion: BaseQuestion =
                        when (dto.type) {
                                QuestionTypes.MCQ ->
                                        MCQ(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                options =
                                                        dto.options
                                                                ?.map {
                                                                        MCQOption(
                                                                                text = it.text,
                                                                                isCorrect =
                                                                                        it.isCorrect
                                                                        )
                                                                }
                                                                ?.toMutableList()
                                                                ?: mutableListOf()
                                        )
                                QuestionTypes.MMCQ ->
                                        MMCQ(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                options =
                                                        dto.options
                                                                ?.map {
                                                                        MCQOption(
                                                                                text = it.text,
                                                                                isCorrect =
                                                                                        it.isCorrect
                                                                        )
                                                                }
                                                                ?.toMutableList()
                                                                ?: mutableListOf()
                                        )
                                QuestionTypes.CODING ->
                                        CodingQuestion(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                driverCode = dto.driverCode,
                                                boilerCode = dto.boilerCode,
                                                functionName = dto.functionName,
                                                returnType = dto.returnType,
                                                params =
                                                        dto.params?.map {
                                                                FunctionParamDTO(it.param, it.type)
                                                        }
                                                                ?: listOf(),
                                                testcases =
                                                        dto.testcases?.map {
                                                                TestCaseDTO(it.input, it.expected,it.tags,it.isMinimal,it.language)
                                                        }
                                                                ?: listOf(),
                                                language = dto.language,
                                                answer = dto.answer
                                        )
                                QuestionTypes.FILL_UP ->
                                        FillUp(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                strictMatch = dto.strictMatch,
                                                llmEval = dto.llmEval,
                                                template = dto.template,
                                                blanks =
                                                        dto.blanks?.map {
                                                                blanks(it.id, it.answers)
                                                        }
                                                                ?: listOf()
                                        )
                                QuestionTypes.DESCRIPTIVE ->
                                        DescriptiveQuestion(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                expectedAnswer = dto.expectedAnswer,
                                                strictness = dto.strictness,
                                                guidelines = dto.guidelines,
                                                answer = dto.answer
                                        )
                                QuestionTypes.FILE_UPLOAD ->
                                        FileUpload(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                expectedAnswer = dto.expectedAnswer,
                                                strictness = dto.strictness,
                                                guidelines = dto.guidelines
                                        )
                                QuestionTypes.MATCH_THE_FOLLOWING ->
                                        MatchTheFollowing(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                keys =
                                                        dto.keys
                                                                ?.map {
                                                                        MatchPair(

                                                                                leftPair =
                                                                                        it.leftPair,
                                                                                rightPair =
                                                                                        it.rightPair
                                                                        )
                                                                }
                                                                ?.toMutableList()
                                                                ?: mutableListOf()
                                        )
                                QuestionTypes.TRUEFALSE ->
                                        TrueFalse(
                                                id = null,
                                                question = dto.question,
                                                bank = bank,
                                                topic = topics.toMutableList(),
                                                explanation = dto.explanation,
                                                hint = dto.hint,
                                                marks = dto.marks,
                                                bloomsTaxonomy = dto.bloomsTaxonomy,
                                                co = dto.co,
                                                negativeMark = dto.negativeMark,
                                                difficulty = dto.difficulty,
                                                answer = dto.trueFalseAnswer ?: false
                                        )
                        }

                return baseQuestion
        }

        fun createBankQuestion(dto: CreateQuestionDTO, bankId: UUID, userId: String) {
                logger.info("Creating bank question for bank: {} by user: {}", bankId, userId)

                // Add this line to validate the question data
                BankValidationUtils.validateQuestionData(dto)

                val user =
                        userRepository.findById(userId).orElseThrow {
                                NotFoundException("User with ID $userId not found")
                        }

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureBankAccess(bank, userId)

                val baseQuestion = createQuestion(dto, bankId)
                val savedQuestion = baseQuestionRepository.save(baseQuestion)

                val bankQuestion = BankQuestion(question = savedQuestion, updateBy = user)

                bankQuestionRepository.save(bankQuestion)
                bank.bankQuestion.add(bankQuestion)
                bankRepository.save(bank)

                logger.info("Successfully created bank question for bank: {}", bankId)
        }
        fun editBankQuestion(
                patchDTO: PatchQuestionDTO,
                questionId: UUID,
                userId: String,
                bankId: UUID
        ) {
                val bankQuestion = bankQuestionRepository.findById(questionId).orElseThrow {
                        BankQuestionNotFoundException(questionId.toString())
                }
                val baseQuestion = bankQuestion.question
                val updatedQuestion = baseQuestion.patchWith(patchDTO)
                        ?: throw IllegalArgumentException("Failed to patch question")
                val user =
                        userRepository.findById(userId).orElseThrow {
                                NotFoundException("User with ID $userId not found")
                        }
                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }
                val savedBaseQuestion = baseQuestionRepository.save(updatedQuestion)

                // Create a new BankQuestion with the updated base question
                val updatedBankQuestion =
                        BankQuestion(
                                id = questionId,
                                question = savedBaseQuestion,
                                updateBy = user,
                                updatedAt = java.time.Instant.now()
                        )
                bankQuestionRepository.save(updatedBankQuestion)

                logger.info("Successfully updated bank question: {}", questionId)
                bankQuestionRepository.save(updatedBankQuestion)
        }


        fun deleteBankQuestion(questionId: UUID, bankId: UUID, userId: String) {
                logger.info(
                        "Deleting bank question: {} from bank: {} by user: {}",
                        questionId,
                        bankId,
                        userId
                )

                val bankQuestion =
                        bankQuestionRepository.findById(questionId).orElseThrow {
                                BankQuestionNotFoundException(questionId.toString())
                        }

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureBankAccess(bank, userId)

                val baseQuestionId =
                        bankQuestion.question.id
                                ?: throw BusinessLogicException("Question ID cannot be null")

                // Remove from bank's question list
                bank.bankQuestion.removeIf { it.id == questionId }
                bankRepository.save(bank)

                // Delete the bank question and base question
                bankQuestionRepository.deleteById(questionId)
                baseQuestionRepository.deleteById(baseQuestionId)

                logger.info(
                        "Successfully deleted bank question: {} from bank: {}",
                        questionId,
                        bankId
                )
        }
        fun getBankCount(): Long {
                return bankRepository.count()
        }
}
