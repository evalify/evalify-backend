package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID

data class TrueFalseResponseDTO(
    override val questionId: UUID,
    val answer : Boolean
) : ResponseDTO(
    questionId = questionId
) {
}