package com.evalify.evalifybackend.course.domain.DTO

import java.util.UUID

data class CourseInstructorPreviewDTO (
    val id: UUID,
    val name:String,
    val courseCode: String,
    val description: String,
    val quizzes:Int,
    val semester: CourseInstructorSemesterDTO
){
}