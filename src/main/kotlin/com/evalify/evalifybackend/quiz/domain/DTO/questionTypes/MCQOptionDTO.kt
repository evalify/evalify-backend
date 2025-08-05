package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.util.UUID
import java.util.UUID.randomUUID

data class MCQOptionDTO @JsonCreator constructor(
    @JsonProperty("id") val id: UUID? = randomUUID(),
    @JsonProperty("text") val text: String
)
