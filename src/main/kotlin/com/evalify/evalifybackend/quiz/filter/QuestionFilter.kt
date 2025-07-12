package com.evalify.evalifybackend.quiz.filter

import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion

interface QuestionFilter {
    fun filter(questions:List<BankQuestion>):List<BankQuestion>
}