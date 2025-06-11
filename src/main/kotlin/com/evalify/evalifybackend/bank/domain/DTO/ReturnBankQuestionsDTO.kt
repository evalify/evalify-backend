package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion

data class ReturnBankQuestionsDTO(
    val questions : List<BankQuestion>,
    val topics : List<TopicDTO>?
) {
}