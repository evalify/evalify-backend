package com.evalify.evalifybackend.bank.domain.DTO.topic

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ReturnTopicDTO @JsonCreator constructor(
    @JsonProperty("id") val id: UUID?,
    @JsonProperty("name") val name: String
)