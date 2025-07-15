package com.evalify.evalifybackend.bank.domain.DTO.bank

import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import java.time.Instant
import java.util.UUID

data class BankQuestionDTO(
    val id: UUID?,
    val question: String,
    val explanation: String?,
    val hint: String?,
    val marks: Int,
    val bloomsTaxonomy: Taxonomy,
    val co: Int,
    val negativeMark: Int?,
    val difficulty: Difficulty,
    val topics: List<ReturnTopicDTO>,
    val questionType: QuestionTypes,
    val updatedAt: Instant,
    val updatedBy: String?
)