package com.evalify.evalifybackend.quiz.domain.DTO.responses

import jakarta.persistence.Embeddable
import java.util.UUID
@Embeddable
open class ResponseDTO(
    open val questionId : UUID
) {
}