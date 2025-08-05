package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MCQOptionDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import kotlin.time.Duration

data class MCQResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId")override val questionId: UUID,
    @JsonProperty("answer")val answer : UUID? = null,
    @JsonProperty("duration")override val duration: Duration

) : ResponseDTO(
    questionId = questionId,
    duration = duration
) {
}