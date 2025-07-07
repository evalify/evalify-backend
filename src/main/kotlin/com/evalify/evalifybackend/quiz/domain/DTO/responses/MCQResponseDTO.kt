package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MCQOptionDTO
import java.util.UUID

data class MCQResponseDTO(
    override val questionId: UUID,
    val answer : MCQOptionDTO? = null
) : ResponseDTO(
    questionId = questionId
) {
}