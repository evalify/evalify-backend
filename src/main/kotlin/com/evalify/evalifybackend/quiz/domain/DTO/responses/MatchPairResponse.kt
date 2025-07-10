package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID

data class MatchPairResponse(
    val leftPairId : UUID,
    val rightPairId : UUID
) {
}