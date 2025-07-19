package com.evalify.evalifybackend.course.controller

import com.evalify.evalifybackend.course.domain.DTO.student.StudentCourseDTO
import com.evalify.evalifybackend.course.service.CourseStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/students/course")
class CourseStudentController(
    private val courseStudentService: CourseStudentService

)
{
    @GetMapping("/me")
    fun getMyCourses(
    ): ResponseEntity<List<StudentCourseDTO>>
    {
        val userId = SecurityUtils.getCurrentUserId()?: throw Exception("User not Found.")
        val result = courseStudentService.getStudentCourses(userId)
        return result


    }
}