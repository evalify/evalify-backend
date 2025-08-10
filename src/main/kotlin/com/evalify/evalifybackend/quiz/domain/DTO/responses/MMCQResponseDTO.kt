package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import kotlin.time.Duration

data class MMCQResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") override val questionId: UUID,
    @JsonProperty("listUUIDAnswer")override val listUUIDAnswer : List<UUID>? = null,
    @JsonProperty("duration") override val duration: Long
): ResponseDTO(
    questionId = questionId,
    duration = duration,
    listUUIDAnswer = listUUIDAnswer
    ) {
}