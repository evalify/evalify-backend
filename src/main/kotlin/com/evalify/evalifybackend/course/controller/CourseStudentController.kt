package com.evalify.evalifybackend.course.controller

import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.course.domain.DTO.CourseStudentPreviewDTO
import com.evalify.evalifybackend.course.service.CourseStudentService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/student/course")
class CourseStudentController(
    private val courseStudentService: CourseStudentService
) {
    @GetMapping
    fun getCourses(): List<CourseStudentPreviewDTO> {
        val studentId = getCurrentUserId()
        println(studentId)
        return courseStudentService.getCoursesByStudentId(studentId)
    }
    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
            ?: throw UnauthorizedException("User not authenticated")
    }
}