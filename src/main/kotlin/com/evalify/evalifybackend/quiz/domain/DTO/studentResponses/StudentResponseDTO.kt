package com.evalify.evalifybackend.quiz.domain.DTO.studentResponses

import java.util.UUID

open class StudentResponseDTO(
    open val questionId: UUID,
    open val duration : Long,
    open val marks : Float? = null,
    open val remarks : String? = null,// Duration in milliseconds
    open val isEvaluated : Boolean? = false,


) {
}
