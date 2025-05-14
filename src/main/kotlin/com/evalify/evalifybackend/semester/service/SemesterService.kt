package com.evalify.evalifybackend.semester.service
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SemesterService (val semesterRepository:SemesterRepository, val userRepository: UserRepository,val courseRepository: CourseRepository){

    fun assignManagersToSemester(semesterId:UUID,managersId:List<UUID>){
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            NotFoundException("Semester with id ${semesterId} not found")
        }
        val managers=userRepository.findAllById(managersId);
        semester.managers.addAll(managers)
        semesterRepository.save(semester)
    }

    fun removeManagersFromSemester(semesterId:UUID,managersId: List<UUID>){
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            NotFoundException("Semester with id ${semesterId} not found")
        }
        val managers=userRepository.findAllById(managersId);
        semester.managers.removeAll(managers)
        semesterRepository.save(semester)
    }
    fun addCourseToSemester(semesterId:UUID,courseId:List<UUID>){
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            NotFoundException("Semester with id ${semesterId} not found")
        }
        val courses=  courseRepository.findAllById(courseId);
        semester.courses.addAll(courses)
        semesterRepository.save(semester)
    }

    fun removeCourseFromSemester(semesterId:UUID,courseId: List<UUID>){
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            NotFoundException("Semester with id ${semesterId} not found")
        }
        val courses=courseRepository.findAllById(courseId);
        semester.courses.removeAll(courses)
        semesterRepository.save(semester)
    }







}