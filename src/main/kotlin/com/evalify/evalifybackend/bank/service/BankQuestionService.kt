package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.bank.exception.BankQuestionNotFoundException
import com.evalify.evalifybackend.bank.util.BankSecurityUtils
import com.evalify.evalifybackend.quiz.question.repository.BankQuestionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class BankQuestionService (
    private val bankQuestionRepository: BankQuestionRepository,
){
    fun getBankQuestionById(questionId: UUID, userId: String): BankQuestionDTO {
        val bankQuestion = bankQuestionRepository.findById(questionId)
            .orElseThrow { BankQuestionNotFoundException(questionId.toString()) }

        // Check if user has access to the bank that contains this question
        val bank = bankQuestion.question.bank
        if (bank == null) {
            throw BankQuestionNotFoundException(questionId.toString())
        }

        // Use proper access control utility
        BankSecurityUtils.ensureBankAccess(bank, userId)

        return BankQuestionDTO(
            id = bankQuestion.id,
            question = bankQuestion.question.question,
            explanation = bankQuestion.question.explanation,
            hint = bankQuestion.question.hint,
            marks = bankQuestion.question.marks,
            bloomsTaxonomy = bankQuestion.question.bloomsTaxonomy,
            co = bankQuestion.question.co,
            negativeMark = bankQuestion.question.negativeMark,
            difficulty = bankQuestion.question.difficulty,
            topics = bankQuestion.question.topic.map { topic ->
                ReturnTopicDTO(
                    id = topic.id,
                    name = topic.name,
                )
            },
            questionType = bankQuestion.question.getQuestionType(),
            updatedAt = bankQuestion.updatedAt,
            updatedBy = bankQuestion.updateBy?.id
        )
    }
}