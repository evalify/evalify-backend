package com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

enum class TestCaseType {
    SAMPLE,
    HIDDEN
}

data class TestCaseDTO @JsonCreator constructor(
    @JsonProperty("code") val code: String,
    @JsonProperty("tags") val tags: TestCaseType,
    @JsonProperty("isMinimal") val isMinimal: Boolean,
    @JsonProperty("language") val language: String
)