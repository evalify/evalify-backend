package com.evalify.evalifybackend.course.service

import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorSemesterDTO
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CourseInstructorService(
    val courseRepository: CourseRepository,
    val userRepository: UserRepository,
) {
    fun getCourseByInstructor(instructorIds: List<String>): List<CourseInstructorPreviewDTO> {
        // Get instructors by their IDs
        val instructors: List<User> = userRepository.findAllById(instructorIds)

        // Find courses associated with these instructors
        val courses: List<Course> = courseRepository.findAllByInstructors(instructors)

        // Map courses to CourseInstructorPreviewDTO
        return courses.map { course ->
            CourseInstructorPreviewDTO(
                id = course.id!!,
                name = course.name,
                description = course.description,
                quizzes = Integer.valueOf(course.quiz.size),
                courses = CourseInstructorSemesterDTO(
                    id = course.semester.id!!,
                    name = course.semester.name
                )
            )
        }
    }
}
