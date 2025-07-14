package com.evalify.evalifybackend.quiz.filter

import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import java.util.UUID

class ExistingQuestionFilter(
    val existingQuestionIds: Set<UUID?>,
) : QuestionFilter {

    override fun filter(questions:List<BankQuestion>): List<BankQuestion> {
        return questions.filter { bankQuestion -> bankQuestion.id !in existingQuestionIds  }
    }

}