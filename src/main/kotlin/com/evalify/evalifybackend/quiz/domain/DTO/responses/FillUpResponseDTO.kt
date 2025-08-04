package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID
import kotlin.time.Duration

data class FillUpResponseDTO(
    override val questionId: UUID,
    val answer : List<BlankResponseDTO>? = null,
    override val duration: Duration

) : ResponseDTO(
    questionId = questionId,
    duration = duration
) {
}