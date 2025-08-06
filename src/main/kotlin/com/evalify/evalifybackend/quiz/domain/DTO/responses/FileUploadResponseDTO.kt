package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class FileUploadResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") override val questionId: UUID,
    @JsonProperty("answer") val answer: String? = null,
    @JsonProperty("duration") override val duration: Long = 0L
) : ResponseDTO(
    questionId = questionId,
    duration = duration
){
}