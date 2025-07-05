package com.evalify.evalifybackend.course.service

import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorSemesterDTO
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.collections.distinctBy
import kotlin.collections.plus

@Service
class CourseInstructorService(
    val courseRepository: CourseRepository,
    val userRepository: UserRepository,
    val semesterRepository: SemesterRepository

) {
    fun getCourseByInstructor(instructorIds: List<String>): List<CourseInstructorPreviewDTO> {
        // Get instructors by their IDs
        val instructors: List<User> = userRepository.findAllById(instructorIds)

        // Find courses associated with these instructors
        val instructorCourses: List<Course> = courseRepository.findAllByInstructors(instructors)

        // Find semesters managed by these instructors
        val managerCourses = mutableListOf<Course>()
        val semesters = semesterRepository.findByManagerId(manager = instructors)

        semesters.stream().forEach {semester->{
            managerCourses.addAll(semester.courses)
        } }

        // Merge the two course lists and remove duplicates
        val allCourses = (instructorCourses + managerCourses).distinctBy { it.id }

        // Map courses to CourseInstructorPreviewDTO
        return allCourses.map { course ->
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
