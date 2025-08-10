package com.evalify.evalifybackend.quiz.domain.DTO.studentResponses

import com.evalify.evalifybackend.quiz.domain.DTO.responses.MatchPairResponse
import java.util.UUID




data class StudentMatchResponseDTO(
    override val questionId : UUID,
    override val duration : Long, // Duration in milliseconds,
    val answer : List<MatchPairResponse>? = null) : StudentResponseDTO(
    questionId = questionId,
    duration = duration
) {
}
