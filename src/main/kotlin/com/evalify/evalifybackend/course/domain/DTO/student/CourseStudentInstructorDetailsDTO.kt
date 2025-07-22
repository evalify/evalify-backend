package com.evalify.evalifybackend.course.domain.DTO.student

import java.util.UUID

data class CourseStudentInstructorDetailsDTO(
    val id:String?,
    val name:String,
    val email:String,
    val phoneNumber:String,
    val image:String?,
    val batch: UUID? = null
) {
}