package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class UpdateQuizCourseDTO(
    val course:List<UUID>
) {
}