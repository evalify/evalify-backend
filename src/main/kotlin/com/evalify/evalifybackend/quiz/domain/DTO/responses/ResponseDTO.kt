package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Embeddable
import java.util.UUID

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
open class ResponseDTO @JsonCreator constructor(
    @JsonProperty("questionId") open val questionId: UUID,
    @JsonProperty("duration") open val duration: Long, // Duration in milliseconds
    @JsonProperty("stringAnswer") open val stringAnswer : String? = null,
    @JsonProperty("uuidAnswer") open val uuidAnswer : UUID? = null,
    @JsonProperty("listUUIDAnswer") open val listUUIDAnswer:List<UUID>? = null,
    @JsonProperty("booleanAnswer") open val booleanAnswer : Boolean? = null,
    @JsonProperty("fillupAnswer") open val fillupAnswer : List<BlankResponseDTO>? = null,
    @JsonProperty("matchAnswer") open val matchAnswer : List<MatchPairResponse>? = null
)
