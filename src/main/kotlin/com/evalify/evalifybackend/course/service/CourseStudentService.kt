package com.evalify.evalifybackend.course.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.domain.DTO.CourseSemesterDTO
import com.evalify.evalifybackend.course.domain.DTO.CourseStudentPreviewDTO
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class CourseStudentService(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) {
    fun getCoursesByStudentId(studentId: String): List<CourseStudentPreviewDTO> {
        val student = userRepository.findById(studentId).orElseThrow{
            NotFoundException("Student $studentId not found")
        }
        val courses = courseRepository.findCourseByStudent(student)
        return courses.map { course-> CourseStudentPreviewDTO(
            course.id,
            course.name,
            course.description,
            course.code,
            course.image,
            course.type,
            CourseSemesterDTO(course.semester.id,course.semester.name,course.semester.year)
        ) }
    }
}