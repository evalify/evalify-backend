package com.evalify.evalifybackend.semester.controller
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.course.domain.DTO.CourseResponse
import com.evalify.evalifybackend.course.domain.DTO.CreateCourseRequest
import com.evalify.evalifybackend.semester.domain.DTO.AddOrRemoveCourseDTO
import com.evalify.evalifybackend.semester.domain.DTO.AssignManagersDTO
import com.evalify.evalifybackend.semester.domain.DTO.SemesterManagerDTO
import com.evalify.evalifybackend.semester.domain.DTO.SemesterRequest
import com.evalify.evalifybackend.semester.domain.DTO.SemesterResponse
import com.evalify.evalifybackend.semester.service.SemesterService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/semester")
class SemesterController(val semesterService: SemesterService) {

    @GetMapping
    fun getAllSemesters(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<SemesterResponse>> {
        return ResponseEntity.ok(
            semesterService.getAllSemestersPaginated(page, size, sort_by, sort_order)
        )
    }

    @PostMapping
    fun createSemester(@RequestBody semesterRequest: SemesterRequest): ResponseEntity<SemesterResponse> {
        val createdSemester = semesterService.createSemester(semesterRequest)
        return ResponseEntity(createdSemester, HttpStatus.CREATED)
    }

    @PutMapping("/{semesterId}")
    fun updateSemester(
        @PathVariable semesterId: UUID,
        @RequestBody semesterRequest: SemesterRequest
    ): ResponseEntity<SemesterResponse> {
        val updatedSemester = semesterService.updateSemester(semesterId, semesterRequest)
        return ResponseEntity(updatedSemester, HttpStatus.OK)
    }

    @DeleteMapping("/{semesterId}")
    fun deleteSemester(@PathVariable semesterId: UUID): ResponseEntity<Void> {
        semesterService.deleteSemester(semesterId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{semesterId}/add-course")
    fun addCourseToSemester(@RequestBody courseDto: AddOrRemoveCourseDTO, @PathVariable semesterId: UUID) {
        semesterService.addCourseToSemester(semesterId = semesterId, courseId = courseDto.courseId)
    }

    @PutMapping("/{semesterId}/remove-course")
    fun removeCourseFromSemester(@RequestBody courseDto: AddOrRemoveCourseDTO, @PathVariable semesterId: UUID) {
        semesterService.removeCourseFromSemester(semesterId = semesterId, courseId = courseDto.courseId)
    }

    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<SemesterResponse>> {
        return ResponseEntity.ok(
            semesterService.searchSemesterPaginated(query, page, size, sort_by, sort_order)
        )
    }

    @GetMapping("/{id}")
    fun getSemesterById(@PathVariable id: UUID): ResponseEntity<SemesterResponse> {
        return ResponseEntity(
            semesterService.getSemesterById(id),
            HttpStatus.OK
        )
    }
    @GetMapping("/{id}/courses")
    fun getCoursesBySemesterId(@PathVariable id: UUID): ResponseEntity<List<CourseResponse>> {
        return ResponseEntity(
            semesterService.getCoursesBySemesterId(id),
            HttpStatus.OK
        )
    }

    @PostMapping("/{id}/courses")
    fun createCourseForSemester(
        @PathVariable id: UUID,
        @RequestBody courseRequest: CreateCourseRequest
    ): ResponseEntity<CourseResponse> {
        val course = semesterService.createCourseForSemester(id, courseRequest)
        return ResponseEntity(course, HttpStatus.CREATED)
    }

    @DeleteMapping("/{semesterId}/courses/{courseId}")
    fun deleteCourseFromSemester(
        @PathVariable semesterId: UUID,
        @PathVariable courseId: UUID
    ): ResponseEntity<CourseResponse> {
        val course = semesterService.deleteCourseFromSemester(semesterId, courseId)
        return ResponseEntity(course, HttpStatus.OK)
    }

    @GetMapping("/{id}/managers")
    fun getSemesterManagers(@PathVariable id: UUID): ResponseEntity<List<SemesterManagerDTO>> {
        val managers = semesterService.getSemesterManagers(id)
        return ResponseEntity.ok(managers)
    }

    @PostMapping("/{semesterId}/managers")
    fun assignManagers(@RequestBody assignManagersDTO: AssignManagersDTO, @PathVariable semesterId: UUID) {
        semesterService.assignManagersToSemester(managersId = assignManagersDTO.managersId, semesterId = semesterId)
    }

    @DeleteMapping("/{semesterId}/managers")
    fun removeManagers(@RequestBody assignManagersDTO: AssignManagersDTO, @PathVariable semesterId: UUID) {
        semesterService.removeManagersFromSemester(managersId = assignManagersDTO.managersId, semesterId = semesterId)
    }

    @GetMapping("/count")
    fun getSemesterCount(): ResponseEntity<Map<String,Long>> {
        val count = semesterService.getSemesterCount()
        return ResponseEntity.ok(mapOf("count" to count))
    }
}