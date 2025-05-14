package com.evalify.evalifybackend.course.controller
import com.evalify.evalifybackend.course.domain.DTO.AssignUserDTO
import com.evalify.evalifybackend.course.domain.DTO.UpdateCourseBatchDTO
import com.evalify.evalifybackend.course.service.CourseService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("course")
class CourseController (val courseService:CourseService){

    @PutMapping("/assign-student")
    fun assignStudentsToCourse(@RequestBody assignUserDTO: AssignUserDTO, @PathVariable courseId:UUID){
        courseService.assignStudent(courseId = courseId, studentId = assignUserDTO.userId)
    }

    @PutMapping("/remove-student")
    fun removeStudentFromCourse(@RequestBody assignUserDTO: AssignUserDTO, @PathVariable courseId:UUID){
        courseService.removeStudent(courseId = courseId, studentId = assignUserDTO.userId)
    }

    @PutMapping("/assign-instructor")
    fun assignInstructor(@RequestBody assignUserDTO: AssignUserDTO,@PathVariable courseId: UUID){
        courseService.assignInstructor(courseId=courseId, instructorId = assignUserDTO.userId)
    }
    @PutMapping("/remove-instructor")
    fun removeInstructor(@RequestBody assignUserDTO: AssignUserDTO,@PathVariable courseId: UUID){
        courseService.removeInstructor(courseId=courseId, instructorId = assignUserDTO.userId)
    }

    @PutMapping("/add-batch")
    fun addBatch(@RequestBody batchDto: UpdateCourseBatchDTO, @PathVariable courseId: UUID){
        courseService.addBatchToCourse(courseId=courseId, batchId = batchDto.batchId)
    }

    @PutMapping("/remove-batch")
    fun removeBatch(@RequestBody batchDto: UpdateCourseBatchDTO, @PathVariable courseId: UUID){
        courseService.removeBatchFromCourse(courseId=courseId, batchId = batchDto.batchId)
    }

}