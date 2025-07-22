package com.evalify.evalifybackend.course.mapper

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.DTO.student.CourseStudentInstructorDTO
import com.evalify.evalifybackend.course.domain.DTO.student.CourseStudentInstructorDetailsDTO
import com.evalify.evalifybackend.user.domain.User
import java.util.UUID

class CourseStudentInstructorMapper {
    fun toCourseStudentsDTO(course: Course):CourseStudentInstructorDTO{
        val courseStudents = course.students.filter { it->it.isActive }.map { student-> toStudentDTO(student)}
        val batchStudents:List<CourseStudentInstructorDetailsDTO> = course.batches.flatMap { batch -> toBatchDTO(batch) }
        return CourseStudentInstructorDTO(
            courseId = course.id,
            students = courseStudents + batchStudents,
        )
    }
    fun toCourseBatchStudentsDTO(batches:List<Batch>,courseId: UUID?):CourseStudentInstructorDTO{
        return CourseStudentInstructorDTO(
            courseId = courseId,
            students = batches.flatMap { it->toBatchDTO(it) }
        )
    }

     fun toBatchDTO(batch: Batch): List<CourseStudentInstructorDetailsDTO>{
        return batch.students.filter { it->it.isActive }.flatMap { it->
            listOf(
                CourseStudentInstructorDetailsDTO(
                    it.id,
                    it.name,
                    it.email,
                    it.phoneNumber,
                    it.image,
                    batch.id
                )
            )
        }
    }

    private fun toStudentDTO(user: User,batchId: UUID?=null):CourseStudentInstructorDetailsDTO{
        return CourseStudentInstructorDetailsDTO(
            user.id,
            user.name,
            user.email,
            user.phoneNumber,
            user.image,
            batchId
        )
    }

}