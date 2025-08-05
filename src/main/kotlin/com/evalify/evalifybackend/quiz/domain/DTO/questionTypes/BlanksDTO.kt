package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonAlias
import java.util.UUID

enum class BlankType{
    LOWERCASE, UPPERCASE, INTEGER, FLOAT, STRING

}

data class BlanksDTO @JsonCreator constructor(
    @JsonProperty("sNo")
    @JsonAlias("sno")
    val sNo: Int,
    @JsonProperty("id") val id: Int,
    @JsonProperty("type") val type: BlankType
)