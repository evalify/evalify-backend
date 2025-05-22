package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.answer.QuestionAnswer
import com.evalify.evalifybackend.questions.domain.Difficulty
import java.util.UUID
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy

data class AddBankQuestionDTO(
    val question: String,
    val topicIds: List<UUID>,
    val BankId : UUID,
    val explanation: String? = null,
    val hint: String? = null,
    val type: QuestionTypes,
    val marks: Int,
    val bloomsTaxonomy: Taxonomy,
    val co: Int,
    val negativeMark: Int? = null,
    val difficulty: Difficulty,
    val answer: QuestionAnswer
)
