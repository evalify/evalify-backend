package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class AddQuestionsResponse(
    val quizId: UUID,
    val sectionId: UUID,
    val addedQuestionCount: Int,
    val addedBankQuestionIds: List<UUID>,
    val message: String

)