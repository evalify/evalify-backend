package com.evalify.evalifybackend.course.service

import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorSemesterDTO
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID
import kotlin.collections.distinctBy
import kotlin.collections.plus

@Service
class CourseInstructorService(
    val courseRepository: CourseRepository,
    val userRepository: UserRepository,
    val semesterRepository: SemesterRepository

) {
    @Transactional
    fun getCourseByInstructor(instructorId: List<String>): List<CourseInstructorPreviewDTO> {
        // Get instructor by ID
        val instructor: User = userRepository.findAllById(instructorId)
            .firstOrNull() ?: throw IllegalArgumentException("Instructor with ID $instructorId not found")


        // Find courses where this user is an instructor
        val instructorCourses: List<Course> = courseRepository.findAllByInstructors(listOf(instructor))

        // Find semesters managed by this instructor
        val managerCourses = mutableListOf<Course>()
        val semesters = semesterRepository.findByManagerId(manager = listOf(instructor))

        semesters.forEach { semester ->
            println("<UNK> <UNK> <UNK> <UNK> ${semester.id} ${semester.name}")
            managerCourses.addAll(semester.courses)
        }

        println(managerCourses)

        // Merge the two course lists and remove duplicates
        val allCourses = (instructorCourses + managerCourses).distinctBy { it.id }

        // Map courses to CourseInstructorPreviewDTO
        return allCourses.map { course ->
            CourseInstructorPreviewDTO(
                id = course.id!!,
                name = course.name,
                courseCode = course.code,
                description = course.description,
                quizzes = course.quiz.size,
                semester = CourseInstructorSemesterDTO(
                    id = course.semester.id!!,
                    name = course.semester.name
                )
            )
        }
    }
}
