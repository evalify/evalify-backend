package com.evalify.evalifybackend.quiz.domain.DTO.studentResponses

import java.util.UUID


data class StudentTrueFalseResponseDTO(
    override val questionId : UUID,
    override val duration : Long, // Duration in milliseconds
    val answer : Boolean? = null
) : StudentResponseDTO(
    questionId = questionId,
    duration = duration
) {
}
