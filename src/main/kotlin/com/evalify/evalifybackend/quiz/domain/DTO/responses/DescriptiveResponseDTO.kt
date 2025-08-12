package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonAlias
import java.util.UUID
import kotlin.time.Duration

data class DescriptiveResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") override val questionId : UUID,
    @JsonProperty("stringAnswer") 
    @JsonAlias("answer") // Allow both "stringAnswer" and "answer" for backward compatibility
    override val stringAnswer: String? ,
    @JsonProperty("duration") override val duration: Long
) : ResponseDTO(
    questionId = questionId,
    duration = duration,
    stringAnswer = stringAnswer
) {
}