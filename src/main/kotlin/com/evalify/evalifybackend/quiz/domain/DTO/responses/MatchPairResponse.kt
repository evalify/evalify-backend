package com.evalify.evalifybackend.quiz.domain.DTO.responses


import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class MatchPairResponse @JsonCreator constructor(
    @JsonProperty("leftPairId") val leftPairId: UUID,
    @JsonProperty("rightPairId") val rightPairId: UUID
)