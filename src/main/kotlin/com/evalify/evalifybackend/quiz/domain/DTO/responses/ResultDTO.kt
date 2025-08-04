package com.evalify.evalifybackend.quiz.domain.DTO.responses

import jakarta.persistence.Embeddable
import java.util.UUID

@Embeddable
data class ResultDTO(
    val questionId : UUID,
    val marks : Float,
    val remarks : String? = null

) {
}