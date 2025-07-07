package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID

data class FillUpResponseDTO(
    override val questionId: UUID,
    val answer : List<BlankResponseDTO>
) : ResponseDTO(
    questionId = questionId
) {
}