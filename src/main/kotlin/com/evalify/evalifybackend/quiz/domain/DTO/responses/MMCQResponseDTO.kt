package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MCQOptionDTO
import java.util.UUID

data class MMCQResponseDTO(
    override val questionId: UUID,
    val answer : List<UUID>? = null

    ): ResponseDTO(
    questionId = questionId
    ) {
}