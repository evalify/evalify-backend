package com.evalify.evalifybackend.bank.domain.DTO.bank

import com.evalify.evalifybackend.bank.domain.DTO.topic.TopicDTO
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion

data class ReturnBankQuestionsDTO(
    val questions : List<BankQuestion>,
    val topics : List<TopicDTO>?
) {
}