package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Embeddable
import java.util.UUID
import kotlin.time.Duration

@Embeddable
open class ResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") open val questionId : UUID,
    @JsonProperty("questionId") open val duration: Duration
) {
}