package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz

import java.util.UUID

data class UpdateQuizCourseDTO(
    val course:List<UUID>
) {
}