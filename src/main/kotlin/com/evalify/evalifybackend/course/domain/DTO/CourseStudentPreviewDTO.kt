package com.evalify.evalifybackend.course.domain.DTO

import com.evalify.evalifybackend.course.domain.CourseType
import com.evalify.evalifybackend.semester.domain.Semester
import java.util.UUID

data class CourseStudentPreviewDTO(
    val id: UUID? = null,
    val name: String,
    val description: String,
    val code: String,
    val image:String?,
    val courseType: CourseType,
    val semester: CourseSemesterDTO,

    ) {
}