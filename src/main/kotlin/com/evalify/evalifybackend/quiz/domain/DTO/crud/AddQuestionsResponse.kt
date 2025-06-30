package com.evalify.evalifybackend.quiz.domain.DTO.crud

import java.util.UUID

data class AddQuestionsResponse(
    val quizId: UUID?,
    val sectionId: UUID,
    val addedQuestionCount: Int,
    val addedBankQuestionIds: List<UUID?>,
    val message: String

)