package com.evalify.evalifybackend.quiz.domain.DTO.studentResponses

import java.util.UUID



data class StudentMCQResponseDTO(
    override val questionId : UUID,
    override val duration : Long, // Duration in milliseconds
    val answer : UUID? = null
) : StudentResponseDTO(
    questionId = questionId,
    duration = duration
) {
}