package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MCQOptionDTO
import java.util.UUID
import kotlin.time.Duration

data class MCQResponseDTO(
    override val questionId: UUID,
    val answer : UUID? = null,
    override val duration: Duration

) : ResponseDTO(
    questionId = questionId,
    duration = duration
) {
}