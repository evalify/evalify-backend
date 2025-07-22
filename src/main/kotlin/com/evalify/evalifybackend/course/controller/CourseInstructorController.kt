package com.evalify.evalifybackend.course.controller

import com.evalify.evalifybackend.core.exception.UnauthorizedException
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.domain.DTO.student.CourseStudentInstructorDTO
import com.evalify.evalifybackend.course.service.CourseInstructorService
import com.evalify.evalifybackend.security.utils.SecurityUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import java.util.UUID

/**
 * Controller managing instructor-specific course operations.
 *
 * Use Cases:
 * - Retrieving instructor's assigned courses
 * - Managing instructor-specific course settings
 * - Course access control for instructors
 * - Instructor course preview functionality
 *
 * This controller handles operations specific to course instructors,
 * providing endpoints for managing their course assignments and related tasks.
 */
@RestController
@RequestMapping("/api/courses")
class CourseInstructorController(
    private val courseInstructorService: CourseInstructorService
) {
    /**
     * Retrieves all courses assigned to the authenticated instructor.
     *
     * Use Cases:
     * - Displaying instructor's course dashboard
     * - Course management overview
     * - Course access verification
     * - Instructor workload management
     *
     * @return List of CourseInstructorPreviewDTO containing course details
     * @throws UnauthorizedException if user is not authenticated
     */
    @GetMapping("/instructors")
    fun getCoursesByInstructor(): List<CourseInstructorPreviewDTO> {
        val instructorId = getCurrentUserId()
        return courseInstructorService.getCourseByInstructor(listOf(instructorId))
    }

    @GetMapping("/students/instructors")
    fun getCourseInstructors():List<CourseStudentInstructorDTO>{
        val instructorId = getCurrentUserId()
        println(instructorId)
        return courseInstructorService.getCourseStudentsByInstructor(instructorId)
    }

    private fun getCurrentUserId(): String {
        return SecurityUtils.getCurrentUserId()
            ?: throw UnauthorizedException("User not authenticated")
    }
}
