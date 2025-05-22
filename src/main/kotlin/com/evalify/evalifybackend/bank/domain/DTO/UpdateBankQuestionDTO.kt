package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.answer.QuestionAnswer
import com.evalify.evalifybackend.questions.domain.Difficulty
import java.util.UUID
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy


data class UpdateBankQuestionDTO(

    val question: String,
    val explanation: String?,
    val hint: String?,
    val type: QuestionTypes,
    val marks: Int,
    val bloomsTaxonomy: Taxonomy,
    val co: Int,
    val negativeMark: Int?,
    val difficulty: Difficulty,
    val answer: QuestionAnswer,
    val topicIds: List<UUID>
)
