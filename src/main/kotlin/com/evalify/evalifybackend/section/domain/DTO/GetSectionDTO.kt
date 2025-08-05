package com.evalify.evalifybackend.section.domain.DTO

import com.evalify.evalifybackend.section.domain.Section
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class GetSectionDTO @JsonCreator constructor(
    @JsonProperty("id") val id: UUID?,
    @JsonProperty("name") val name: String
)