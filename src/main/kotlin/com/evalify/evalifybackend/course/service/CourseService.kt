package com.evalify.evalifybackend.course.service
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CourseService (private val courseRepository:CourseRepository,val userRepository: UserRepository,val batchRepository: BatchRepository){
    fun assignStudent(courseId:UUID,studentId:List<UUID>){
        val course = courseRepository.findById(courseId).orElseThrow{
            NotFoundException("course with id $courseId not found")
        }
        val users = userRepository.findAllById(studentId);
        course.student.addAll(users);
        courseRepository.save(course)
    }

    fun removeStudent(courseId:UUID,studentId:List<UUID>){
        val course = courseRepository.findById(courseId).orElseThrow{
            NotFoundException("course with id $courseId not found")
        }
        val users = userRepository.findAllById(studentId);
        course.student.removeAll(users)
        courseRepository.save(course)
    }

    fun assignInstructor(courseId:UUID,instructorId:List<UUID>){
        val course = courseRepository.findById(courseId).orElseThrow{
            NotFoundException("course with id $courseId not found")
        }
        val users = userRepository.findAllById(instructorId);
        course.instructors.addAll(users);
        courseRepository.save(course)
    }

    fun removeInstructor(courseId:UUID,instructorId:List<UUID>){
        val course = courseRepository.findById(courseId).orElseThrow{
            NotFoundException("course with id $courseId not found")
        }
        val users = userRepository.findAllById(instructorId);
        course.instructors.removeAll(users)
        courseRepository.save(course)
    }

    fun addBatchToCourse(courseId:UUID,batchId:List<UUID>){
        val course = courseRepository.findById(courseId).orElseThrow{
            NotFoundException("course with id $courseId not found")
        }
        val batch = batchRepository.findAllById(batchId)
        course.batches.addAll(batch)
        courseRepository.save(course)
    }

    fun removeBatchFromCourse(courseId: UUID,batchId:List<UUID>){
        val course = courseRepository.findById(courseId).orElseThrow{
            NotFoundException("course with id $courseId not found")
        }
        val batch = batchRepository.findAllById(batchId)
        course.batches.removeAll(batch)
        courseRepository.save(course)
    }
}