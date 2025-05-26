package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class AddQuestionsResponse(
    val quizId: UUID,
    val addedQuestionCount: Int,
    val addedBankQuestionIds: List<UUID>,
    val message: String
)

