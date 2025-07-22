package com.evalify.evalifybackend.course.service

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorPreviewDTO
import com.evalify.evalifybackend.course.domain.DTO.CourseInstructorSemesterDTO
import com.evalify.evalifybackend.course.domain.DTO.student.CourseStudentInstructorDTO
import com.evalify.evalifybackend.course.domain.DTO.student.CourseStudentInstructorDetailsDTO
import com.evalify.evalifybackend.course.mapper.CourseStudentInstructorMapper
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.springframework.stereotype.Service
import kotlin.collections.distinctBy
import kotlin.collections.plus

@Service
@Transactional
class CourseInstructorService(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val semesterRepository: SemesterRepository

) {
    fun getCourseByInstructor(instructorId: List<String>): List<CourseInstructorPreviewDTO> {
        // Get instructor by ID
        val instructor: User = userRepository.findAllById(instructorId)
            .firstOrNull() ?: throw IllegalArgumentException("Instructor with ID $instructorId not found")


        // Find courses where this user is an instructor
        val instructorCourses: List<Course> = courseRepository.findAllByInstructor(instructor)

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

    fun getCourseStudentsByInstructor(instructorId: String): List<CourseStudentInstructorDTO> {
        // Get instructor by ID
        val instructor: User = userRepository.findById(instructorId).orElseThrow{ NotFoundException("Instructor with ID $instructorId not found") }

        // Find courses where this user is an instructor
        val instructorCourses: List<Course> = courseRepository.findAllByInstructor(instructor)
        println(instructorCourses.size)
        println("logging....")

        //CourseStudentInstructorDTO mapper
        val courseStudentInstructorMapper = CourseStudentInstructorMapper()

        //mapping
        val courseStudents:List<CourseStudentInstructorDTO> = instructorCourses.filter { it->it.semester.isActive }.map { it->courseStudentInstructorMapper.toCourseStudentsDTO(course = it) }
        return courseStudents

    }
}
