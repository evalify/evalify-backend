package com.evalify.evalifybackend.quiz.filter

import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion

class QuestionTypeFilter(
    val questionTypes:List<QuestionTypes>?
) : QuestionFilter {
    override fun filter(questions: List<BankQuestion>): List<BankQuestion> {
        return if(questionTypes != null)  questions.filter { bankQuestion -> bankQuestion.question.getQuestionType() in questionTypes } else questions
    }
}