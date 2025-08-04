package com.evalify.evalifybackend.quiz.domain.DTO.responses

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MatchShuffleDTO
import java.util.UUID
import kotlin.time.Duration

data class MatchResponseDTO(
    override val questionId: UUID,
    val answer : List<MatchPairResponse>? = null,
    override val duration: Duration


) : ResponseDTO(
    questionId = questionId,
    duration = duration
    ) {
}