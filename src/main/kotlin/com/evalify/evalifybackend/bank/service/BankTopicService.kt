package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.DTO.topic.CreateTopicDTO
import com.evalify.evalifybackend.bank.exception.BankNotFoundException
import com.evalify.evalifybackend.bank.exception.TopicNotFoundException
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.bank.util.BankSecurityUtils
import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.exception.ConflictException
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.topic.repository.TopicRepo
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BankTopicService(
        private val bankRepository: BankRepository,
        private val topicRepo: TopicRepo
) {

    private val logger by logger()

    fun addTopic(dto: CreateTopicDTO, bankId: UUID, userId: String): CreateTopicDTO {
        logger.info("Adding topic '{}' to bank: {} by user: {}", dto.name, bankId, userId)

        val bank =
                bankRepository.findById(bankId).orElseThrow {
                    BankNotFoundException(bankId.toString())
                }

        BankSecurityUtils.ensureBankAccess(bank, userId)

        // Check for duplicate topic names within the bank
        val existingTopic = bank.topics?.find { it.name.equals(dto.name, ignoreCase = true) }
        if (existingTopic != null) {
            logger.warn("Topic with name '{}' already exists in bank: {}", dto.name, bankId)
            throw ConflictException("Topic with name '${dto.name}' already exists in this bank")
        }

        val topic = Topic(name = dto.name, bank = bank)

        val savedTopic = topicRepo.save(topic)
        bank.topics?.add(savedTopic)
        bankRepository.save(bank)

        logger.info("Successfully added topic '{}' to bank: {}", dto.name, bankId)
        return CreateTopicDTO(name = savedTopic.name)
    }

    fun removeTopic(bankId: UUID, topicId: UUID, userId: String) {
        logger.info("Removing topic: {} from bank: {} by user: {}", topicId, bankId, userId)

        val bank =
                bankRepository.findById(bankId).orElseThrow {
                    BankNotFoundException(bankId.toString())
                }

        BankSecurityUtils.ensureBankAccess(bank, userId)

        val topic =
                topicRepo.findById(topicId).orElseThrow {
                    TopicNotFoundException(topicId.toString())
                }

        // Verify a topic belongs to the bank
        if (topic.bank?.id != bankId) {
            logger.warn("Topic {} does not belong to bank {}", topicId, bankId)
            throw ConflictException("Topic does not belong to this bank")
        }

        // Check if a topic is being used by questions
        val questionsUsingTopic =
                bank.bankQuestion.any { bankQuestion ->
                    bankQuestion.question.topic.any { it.id == topicId }
                }

        if (questionsUsingTopic) {
            logger.warn(
                    "Cannot delete topic {} as it's being used by questions in bank {}",
                    topicId,
                    bankId
            )
            throw ConflictException("Cannot delete topic as it's being used by questions")
        }

        bank.topics?.removeIf { it.id == topicId }
        topicRepo.deleteById(topicId)
        bankRepository.save(bank)

        logger.info("Successfully removed topic: {} from bank: {}", topicId, bankId)
    }

    fun editTopic(
            bankId: UUID,
            topicId: UUID,
            topicDto: CreateTopicDTO,
            userId: String
    ): CreateTopicDTO {
        logger.info("Editing topic: {} in bank: {} by user: {}", topicId, bankId, userId)

        val bank =
                bankRepository.findById(bankId).orElseThrow {
                    BankNotFoundException(bankId.toString())
                }

        BankSecurityUtils.ensureBankAccess(bank, userId)

        val existingTopic =
                topicRepo.findById(topicId).orElseThrow {
                    TopicNotFoundException(topicId.toString())
                }

        // Verify a topic belongs to the bank
        if (existingTopic.bank?.id != bankId) {
            logger.warn("Topic {} does not belong to bank {}", topicId, bankId)
            throw ConflictException("Topic does not belong to this bank")
        }

        // Check for duplicate topic names (excluding a current topic)
        val duplicateTopic =
                bank.topics?.find {
                    it.id != topicId && it.name.equals(topicDto.name, ignoreCase = true)
                }
        if (duplicateTopic != null) {
            logger.warn("Topic with name '{}' already exists in bank: {}", topicDto.name, bankId)
            throw ConflictException(
                    "Topic with name '${topicDto.name}' already exists in this bank"
            )
        }

        // Create a new topic with updated name (since name is val in entity)
        val updatedTopic = Topic(id = topicId, name = topicDto.name, bank = bank)
        val savedTopic = topicRepo.save(updatedTopic)

        logger.info("Successfully updated topic: {} in bank: {}", topicId, bankId)
        return CreateTopicDTO(name = savedTopic.name)
    }
}
