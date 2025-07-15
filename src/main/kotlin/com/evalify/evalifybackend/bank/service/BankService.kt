package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.BankUser
import com.evalify.evalifybackend.bank.domain.BankUserId
import com.evalify.evalifybackend.bank.domain.DTO.bank.CopyBankQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.CreateBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.EditBankDTO
import com.evalify.evalifybackend.bank.exception.BankAlreadySharedException
import com.evalify.evalifybackend.bank.exception.BankNotFoundException
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.bank.util.BankSecurityUtils
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.GetSharedUsersDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedUserDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SimpleUserDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID
import org.springframework.transaction.annotation.Transactional
import com.evalify.evalifybackend.quiz.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo

@Service
@Transactional
class BankService(
        private val bankRepository: BankRepository,
        private val userRepository: UserRepository,
        private val baseQuestionRepository: QuestionRepository,
        private val bankQuestionRepository: BankQuestionRepository,
        private val topicRepository: TopicRepo
) {

        private val logger by logger()

        fun createBank(dto: CreateBankDTO, userId: String): CreateBankDTO {
                logger.info("Creating bank '{}' for user: {}", dto.name, userId)

                val user =
                        userRepository.findById(userId).orElseThrow {
                                NotFoundException("User with ID $userId not found")
                        }

                val bank =
                        Bank(
                                name = dto.name,
                                semester = dto.semester,
                                createdAt = Instant.now(),
                                courseCode = dto.courseCode,
                                createdBy = user
                        )

                val bankUser =
                        BankUser(
                                id =
                                        BankUserId(
                                                bank.id,
                                                user.id
                                                        ?: throw NotFoundException(
                                                                "User ID cannot be null"
                                                        )
                                        ),
                                bank = bank,
                                user = user,
                                tags = SharedTags.OWNER
                        )
                bank.sharedUsers.add(bankUser)

                val savedBank = bankRepository.save(bank)
                logger.info(
                        "Successfully created bank with ID: {} for user: {}",
                        savedBank.id,
                        userId
                )

                return CreateBankDTO(
                        name = savedBank.name,
                        courseCode = savedBank.courseCode,
                        semester = savedBank.semester
                )
        }

        fun deleteBank(bankId: UUID, userId: String) {
                logger.info("Deleting bank: {} by user: {}", bankId, userId)

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureOwnership(bank, userId)

                bankRepository.deleteById(bankId)
                logger.info("Successfully deleted bank: {} by user: {}", bankId, userId)
        }

        fun editBank(dto: EditBankDTO, userId: String, bankId: UUID): CreateBankDTO {
                logger.info("Editing bank: {} by user: {}", bankId, userId)

                val existingBank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureOwnership(existingBank, userId)

                val updatedBank =
                        Bank(
                                id = bankId,
                                name = dto.name?:existingBank.name,
                                semester = dto.semester?:existingBank.semester,
                                courseCode = dto.courseCode?:existingBank.courseCode,
                                createdBy = existingBank.createdBy,
                                createdAt = existingBank.createdAt,
                                sharedUsers = existingBank.sharedUsers,
                                topics = existingBank.topics,
                                bankQuestion = existingBank.bankQuestion
                        )

                val savedBank = bankRepository.save(updatedBank)
                logger.info("Successfully updated bank: {} by user: {}", bankId, userId)

                return CreateBankDTO(
                        name = savedBank.name,
                        courseCode = savedBank.courseCode,
                        semester = savedBank.semester
                )
        }

        fun shareBank(bankId: UUID, dto: ShareQuizDTO, userId: String) {
                logger.info(
                        "Sharing bank: {} with users: {} by user: {}",
                        bankId,
                        dto.userID,
                        userId
                )

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureOwnership(bank, userId)

                val usersToShare = userRepository.findAllById(dto.userID)
                if (usersToShare.size != dto.userID.size) {
                        val foundIds = usersToShare.map { it.id }
                        val missingIds = dto.userID.filterNot { foundIds.contains(it) }
                        throw NotFoundException("Users not found: $missingIds")
                }

                var sharedCount = 0
                usersToShare.forEach { user ->
                        val userIdValue =
                                user.id ?: throw NotFoundException("User ID cannot be null")

                        // Check if already shared
                        val alreadyShared = bank.sharedUsers.any { it.user?.id == userIdValue }
                        if (alreadyShared) {
                                logger.warn(
                                        "Bank {} is already shared with user {}",
                                        bankId,
                                        userIdValue
                                )
                                throw BankAlreadySharedException(bankId.toString(), userIdValue)
                        }

                        val bankUser =
                                BankUser(
                                        id = BankUserId(bankId, userIdValue),
                                        bank = bank,
                                        user = user,
                                        tags = SharedTags.SHARED
                                )
                        bank.sharedUsers.add(bankUser)
                        sharedCount++
                }

                bankRepository.save(bank)
                logger.info("Successfully shared bank: {} with {} users", bankId, sharedCount)
        }

        fun unshareBank(bankId: UUID, dto: ShareQuizDTO, userId: String) {
                logger.info(
                        "Unsharing bank: {} from users: {} by user: {}",
                        bankId,
                        dto.userID,
                        userId
                )

                val bank =
                        bankRepository.findById(bankId).orElseThrow {
                                BankNotFoundException(bankId.toString())
                        }

                BankSecurityUtils.ensureOwnership(bank, userId)

                var unsharedCount = 0
                dto.userID.forEach { userIdToUnshare ->
                        val removed =
                                bank.sharedUsers.removeIf {
                                        it.user?.id == userIdToUnshare &&
                                                it.tags == SharedTags.SHARED
                                }
                        if (removed) unsharedCount++
                }

                bankRepository.save(bank)
                logger.info("Successfully unshared bank: {} from {} users", bankId, unsharedCount)
        }

        fun getShareBank(bankId: UUID, userId: String): GetSharedUsersDTO {
            logger.debug("Retrieving shared users for bank: {} by user: {}", bankId, userId)

            val bank =
                bankRepository.findById(bankId).orElseThrow {
                    BankNotFoundException(bankId.toString())
                }

            BankSecurityUtils.ensureBankAccess(bank, userId)

            val users =
                bank.sharedUsers.map { bankUser ->
                    // Create SimpleUserDTO to avoid serialization issues with Hibernate
                    // proxies
                    val simpleUser =
                        bankUser.user?.let { user ->
                            // Force initialization if needed
                            val actualUser =
                                if (user.id != null) {
                                    userRepository
                                        .findById(user.id!!)
                                        .orElse(null)
                                } else null

                            actualUser?.let {
                                SimpleUserDTO(
                                    id = it.id!!,
                                    name = it.name,
                                    email = it.email,
                                    profileId = it.profileId
                                )
                            }
                        }
                    SharedUserDTO(user = simpleUser, tag = bankUser.tags)
                }
            logger.debug("Retrieved {} shared users for bank: {}", users.size, bankId)
            return GetSharedUsersDTO(users) }

    fun copyFromBank(bankId: UUID, dto: CopyBankQuestionDTO, userId: String) {
        logger.info("Copying/moving questions from bank: {} to bank: {} by user: {}", bankId, dto.bankId, userId)

        val fromBank = bankRepository.findById(bankId).orElseThrow {
            BankNotFoundException(bankId.toString())
        }
        val toBank = bankRepository.findById(dto.bankId).orElseThrow {
            BankNotFoundException(dto.bankId.toString())
        }

        BankSecurityUtils.ensureOwnership(toBank, userId)

        val questionsToCopy = fromBank.bankQuestion.filter { dto.questionIds.contains(it.id) }
        if (questionsToCopy.isEmpty()) {
            throw NotFoundException("No questions found to copy/move from bank $bankId with ids ${dto.questionIds}")
        }

        // Create and save new question instances with copied data
        val copiedQuestions = questionsToCopy.map { originalQuestion ->
            // Copy the base question first
            val copiedBaseQuestion = with(originalQuestion.question) {
                copyQuestion().also { copied ->
                    copied.topic.clear()
                    if (dto.createNewTopic) {
                        // Create new topics with same properties but for the target bank
                        val newTopics = topic.map { originalTopic ->
                            Topic(
                                name = originalTopic.name,
                                bank = toBank
                            ).let { newTopic ->
                                topicRepository.save(newTopic)
                            }
                        }
                        copied.topic.addAll(newTopics)
                    }
                }
            }

            // Save the copied base question first
            val savedBaseQuestion = baseQuestionRepository.save(copiedBaseQuestion)

            // Create and save the new bank question with the saved base question
            BankQuestion(
                id = null,
                question = savedBaseQuestion,
                updateBy = originalQuestion.updateBy
            ).also { bankQuestion ->
                bankQuestionRepository.save(bankQuestion)
            }
        }

        // Add the saved copied questions to target bank
        toBank.bankQuestion.addAll(copiedQuestions)

        // If move is true, remove original questions from source bank
        if (dto.move) {
            fromBank.bankQuestion.removeIf { dto.questionIds.contains(it.id) }
            bankRepository.save(fromBank)
        }

        // Save target bank with new questions
        bankRepository.save(toBank)
        logger.info("Successfully copied/moved {} questions from bank {} to bank {}", copiedQuestions.size, bankId, dto.bankId)
    }
}

