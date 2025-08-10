package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import kotlin.time.Duration

data class MCQResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") override val questionId: UUID,
    @JsonProperty("uuidAnswer") override val uuidAnswer : UUID? = null,
    @JsonProperty("duration") override val duration: Long
) : ResponseDTO(
    questionId = questionId,
    duration = duration,
    uuidAnswer = uuidAnswer
) {
}