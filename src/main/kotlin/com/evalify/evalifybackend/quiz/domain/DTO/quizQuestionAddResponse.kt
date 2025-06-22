package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class quizQuestionAddResponse(
    val quizId: UUID?,
    val sectionId: UUID,
    val message: String) {

}