package com.evalify.evalifybackend.course.domain.DTO.student

import com.evalify.evalifybackend.user.domain.dto.UserInfo
import java.util.UUID

data class StudentCourseDTO(
    val id: UUID?,
    val courseName: String,
    val courseCode: String,
    val noOfQuizzes: Int,
    val instructors: List<UserInfo>,
    val semesterId: UUID?,
    val semesterName: String

)