package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.DTO.CreateTopicDTO
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.topic.repository.TopicRepo
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class BankTopicService(private val bankRepository: BankRepository, private val topicRepo: TopicRepo) {

    @Transactional
    fun addTopic(dto: CreateTopicDTO,bankId: UUID): CreateTopicDTO{

        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }

        val topic = Topic(
            name = dto.name,
            bank = bank

        )
        topicRepo.save(topic)
        bank.topics?.add(topic)
        return CreateTopicDTO(
            name = topic.name
        )

    }


    @Transactional
    fun removeTopic(bankId: UUID, topicId: UUID){

        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }

        val topic = topicRepo.findById(topicId).orElseThrow { RuntimeException("Topic not found") }

        bank.topics?.remove(topic)
        topicRepo.deleteById(topicId)

    }

    @Transactional
    fun editTopic(bankId : UUID, topic: CreateTopicDTO , topicId: UUID) : CreateTopicDTO {

        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }
        val topic = Topic(
            name = topic.name,
            bank = bank,
            id = topicId
        )

        topicRepo.save(topic)
        bank.topics?.add(topic)

        return CreateTopicDTO(
            name = topic.name
        )

    }


}