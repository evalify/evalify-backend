package com.evalify.evalifybackend.quiz.filter

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion

class DifficultyLevelFilter(
    val difficult:List<Difficulty>?
) : QuestionFilter {
    override fun filter(questions:List<BankQuestion>): List<BankQuestion> {
        return if(difficult != null) questions.filter { bankQuestion -> bankQuestion.question.difficulty in difficult } else questions
    }
}