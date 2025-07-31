package com.evalify.evalifybackend.quiz.domain.DTO.manager

import java.time.Instant
import java.util.UUID

data class QuizStudentsManagerDTO(
    val id: UUID? = null,
    val name: String,
    val description: String? = null,
    val students:List<QuizStudentsDTO>,
    val lab:List<QuizLabDTO>,
    val createdAt: Instant,

    ) {
}