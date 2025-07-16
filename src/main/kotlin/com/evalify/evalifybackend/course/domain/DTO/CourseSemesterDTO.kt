package com.evalify.evalifybackend.course.domain.DTO

import java.util.UUID

data class CourseSemesterDTO(
    val id: UUID? = null,
    val name:String,
    val year: Int,
) {
}