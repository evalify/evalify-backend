package com.evalify.evalifybackend.quiz.filter

import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion

class FilterManager {
    val filters: MutableList<QuestionFilter> = mutableListOf<QuestionFilter>()

    fun addFilter(filter: QuestionFilter):FilterManager {
        filters.add(filter)
        return this
    }

    fun applyFilter(questions: List<BankQuestion>): List<BankQuestion> {
        return filters.fold(questions){acc, filter -> filter.filter(acc)}
    }
}