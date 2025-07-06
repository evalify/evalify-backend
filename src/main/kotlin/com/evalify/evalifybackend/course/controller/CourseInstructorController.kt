package com.evalify.evalifybackend.course.controller

import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.service.CourseInstructorService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID

@RestController
@RequestMapping("/api/courses/instructors")
class CourseInstructorController(
    private val courseInstructorService: CourseInstructorService
) {
    @GetMapping()
    fun getCoursesByInstructor(): List<CourseInstructorPreviewDTO> {
        val instructorId = getCurrentUserId()
        return courseInstructorService.getCourseByInstructor(listOf(instructorId))
    }

    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
            ?: throw UnauthorizedException("User not authenticated")
    }
}
