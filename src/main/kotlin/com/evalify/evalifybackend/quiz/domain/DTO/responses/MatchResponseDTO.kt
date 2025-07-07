package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MatchShuffleDTO
import java.util.UUID

data class MatchResponseDTO(
    override val questionId: UUID,
    val answer : List<MatchShuffleDTO>? = null

    ) : ResponseDTO(
    questionId = questionId
    ) {
}