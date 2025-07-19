package com.evalify.evalifybackend.course.service

import com.evalify.evalifybackend.course.domain.DTO.student.StudentCourseDTO
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.user.domain.dto.UserInfo
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
@Transactional
class CourseStudentService(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) {
    fun getStudentCourses(studentId: String): ResponseEntity<List<StudentCourseDTO>> {
        val student = userRepository.findById(studentId)
            .orElseThrow { Exception("User not found") }

        val courses = courseRepository.findAllByStudent(student)

        val result = courses.map { course ->
            val instructors = course.instructors.map { instructor ->
                UserInfo(
                    id = instructor.id,
                    name = instructor.name,
                    email = instructor.email
                )
            }

            StudentCourseDTO(
                id = course.id,
                courseName = course.name,
                courseCode = course.code,
                noOfQuizzes = course.quiz.size,
                instructors = instructors
            )
        }

        return ResponseEntity.ok(result)
    }



}