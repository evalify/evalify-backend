package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID

data class CodingResponseDTO(
    override val questionId: UUID,
    val answer : String? = null
) : ResponseDTO(
    questionId = questionId
) {
}