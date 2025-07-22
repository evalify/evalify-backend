package com.evalify.evalifybackend.course.domain.DTO.student

import java.util.UUID

data class CourseStudentInstructorDTO (
    val courseId: UUID?,
    val students:List<CourseStudentInstructorDetailsDTO>,
){

}