package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID
import kotlin.time.Duration

data class TrueFalseResponseDTO(
    override val questionId: UUID,
    val answer : Boolean? = null,
    override val duration: Duration

) : ResponseDTO(
    questionId = questionId,
    duration = duration
) {
}