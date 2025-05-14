package com.evalify.evalifybackend.semester.controller
import com.evalify.evalifybackend.semester.domain.DTO.AddOrRemoveCourseDTO
import com.evalify.evalifybackend.semester.domain.DTO.AssignManagersDTO
import com.evalify.evalifybackend.semester.service.SemesterService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("semester")
class SemesterController (val semesterService: SemesterService){

@PutMapping("/{semesterId}/assign-manager")
fun assignManagers(@RequestBody assignManagersDTO: AssignManagersDTO,@PathVariable semesterId:UUID){
    semesterService.assignManagersToSemester(managersId = assignManagersDTO.managersId, semesterId = semesterId)
}

@PutMapping("/{semesterId}/remove-manager")
    fun removeManagers(@RequestBody assignManagersDTO: AssignManagersDTO,@PathVariable semesterId:UUID){
        semesterService.removeManagersFromSemester(managersId = assignManagersDTO.managersId, semesterId = semesterId)
    }

    @PutMapping("/{semesterId}/add-course")
    fun addCourseToSemester(@RequestBody courseDto:AddOrRemoveCourseDTO,@PathVariable semesterId: UUID){
        semesterService.addCourseToSemester(semesterId = semesterId, courseId = courseDto.courseId)
    }
    @PutMapping("/{semesterId}/remove-course")
    fun removeCourseFromSemester(@RequestBody courseDto:AddOrRemoveCourseDTO,@PathVariable semesterId: UUID){
        semesterService.removeCourseFromSemester(semesterId = semesterId, courseId = courseDto.courseId)
    }

}