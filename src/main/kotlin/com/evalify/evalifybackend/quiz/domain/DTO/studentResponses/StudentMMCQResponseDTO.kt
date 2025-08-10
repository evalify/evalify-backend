package com.evalify.evalifybackend.quiz.domain.DTO.studentResponses

import java.util.UUID



data class StudentMMCQResponseDTO(
    override val questionId : UUID,
    override val duration : Long, // Duration in milliseconds,
    val answer : List<UUID>? = null,) : StudentResponseDTO(
    questionId = questionId,
    duration = duration
) {
}
