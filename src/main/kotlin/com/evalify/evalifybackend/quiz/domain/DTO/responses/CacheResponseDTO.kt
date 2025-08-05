package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.UUID

/**
 * Generic DTO for receiving quiz response data from client.
 * This DTO can handle any type of response data without requiring specific type information.
 * The actual ResponseDTO type will be determined by looking up the question type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CacheResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") val questionId: UUID,
    @JsonProperty("duration") val duration: Long = 0L,
    @JsonProperty("answer") val answer: Any? = null
)
