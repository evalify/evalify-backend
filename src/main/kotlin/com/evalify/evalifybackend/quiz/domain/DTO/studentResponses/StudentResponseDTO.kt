package com.evalify.evalifybackend.quiz.domain.DTO.studentResponses

import java.util.UUID

open class StudentResponseDTO(
    open val questionId: UUID,
    open val duration : Long // Duration in milliseconds


) {
}
