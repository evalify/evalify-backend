package com.evalify.evalifybackend.quiz.domain.DTO.crud

import java.util.UUID

data class AddBankQuestionDTO(
    val sectionId: UUID,
    val bankQuestionId: List<UUID>,

) {
}