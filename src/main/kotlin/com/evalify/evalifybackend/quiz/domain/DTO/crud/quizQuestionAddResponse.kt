package com.evalify.evalifybackend.quiz.domain.DTO.crud

import java.util.UUID

data class quizQuestionAddResponse(
    val quizId: UUID?,
    val sectionId: UUID,
    val message: String) {

}