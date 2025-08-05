package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.PairDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MatchShuffleDTO @JsonCreator constructor(
    @JsonProperty("left") val left: MutableList<PairDTO>?,
    @JsonProperty("right") val right: MutableList<PairDTO>?
)