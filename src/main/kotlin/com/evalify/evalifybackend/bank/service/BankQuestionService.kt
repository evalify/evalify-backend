package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
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
    fun getBankQuestionById(questionId: UUID, userId: String): BankQuestionsReturnDTO {
        val bankQuestion = bankQuestionRepository.findById(questionId)
            .orElseThrow { BankQuestionNotFoundException(questionId.toString()) }

        // Check if user has access to the bank that contains this question
        val bank = bankQuestion.question.bank
        if (bank == null) {
            throw BankQuestionNotFoundException(questionId.toString())
        }

        // Use proper access control utility
        BankSecurityUtils.ensureBankAccess(bank, userId)

        return bankQuestion.question.mapToBankType(bankQuestion.question.id)
    }
}