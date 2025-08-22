package com.evalify.evalifybackend.quiz.domain.DTO.evaluation

import com.fasterxml.jackson.databind.JsonNode
import java.util.UUID

data class QuizResultDTO(
    val questionId: UUID,
    val answer: JsonNode?, // Using JsonNode to handle different answer types
    val marks: Float?,
    val remarks: String?
)
