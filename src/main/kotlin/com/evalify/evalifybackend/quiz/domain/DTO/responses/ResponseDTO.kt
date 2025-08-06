package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Embeddable
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
open class ResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") open val questionId: UUID,
    @JsonProperty("duration") open val duration: Long = 0L // Duration in milliseconds
)
