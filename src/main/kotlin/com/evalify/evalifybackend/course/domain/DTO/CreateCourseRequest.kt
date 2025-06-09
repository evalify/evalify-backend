package com.evalify.evalifybackend.course.domain.DTO

import com.evalify.evalifybackend.course.domain.CourseType
import java.util.UUID

data class CreateCourseRequest(
    val name: String,
    val code: String = "",
    val description: String = "",
    val type: CourseType = CourseType.CORE
)
