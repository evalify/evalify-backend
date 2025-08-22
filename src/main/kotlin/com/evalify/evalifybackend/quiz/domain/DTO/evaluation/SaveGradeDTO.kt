package com.evalify.evalifybackend.quiz.domain.DTO.evaluation

import java.util.UUID

data class SaveGradeDTO(
    val questionId : UUID,
    val marks : Float?,
    val remarks : String? = null
) {
}