package com.evalify.evalifybackend.course.controller

import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.service.CourseInstructorService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID

@RestController
@RequestMapping("/api/v1/courses/instructors")
class CourseInstructorController(
    private val courseInstructorService: CourseInstructorService
) {
    @GetMapping("/{instructorId}")
    fun getCoursesByInstructor(@PathVariable instructorId: String): List<CourseInstructorPreviewDTO> {
        return courseInstructorService.getCourseByInstructor(listOf(instructorId))
    }

    @PostMapping("/batch")
    fun getCoursesByInstructors(@RequestBody instructorIds: List<String>): List<CourseInstructorPreviewDTO> {
        return courseInstructorService.getCourseByInstructor(instructorIds)
    }
}
