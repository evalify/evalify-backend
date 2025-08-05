package com.evalify.evalifybackend.bank.domain.DTO.questionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.Pair
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class PairDTO @JsonCreator constructor(
    @JsonProperty("id") val id: UUID,
    @JsonProperty("text") val text: String
)