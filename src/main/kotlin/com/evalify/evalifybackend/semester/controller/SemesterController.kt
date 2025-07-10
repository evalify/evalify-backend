package com.evalify.evalifybackend.semester.controller
import com.evalify.evalifybackend.core.exception.NotFoundException
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
        return try {
            val createdSemester = semesterService.createSemester(semesterRequest)
            ResponseEntity(createdSemester, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @PutMapping("/{semesterId}")
    fun updateSemester(
        @PathVariable semesterId: UUID,
        @RequestBody semesterRequest: SemesterRequest
    ): ResponseEntity<SemesterResponse> {
        return try {
            val updatedSemester = semesterService.updateSemester(semesterId, semesterRequest)
            ResponseEntity(updatedSemester, HttpStatus.OK)
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @DeleteMapping("/{semesterId}")
    fun deleteSemester(@PathVariable semesterId: UUID): ResponseEntity<Void> {
        return try {
            semesterService.deleteSemester(semesterId)
            ResponseEntity.noContent().build()
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build()
        }
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
        return try {
                val course = semesterService.createCourseForSemester(id, courseRequest)
                ResponseEntity(course, HttpStatus.CREATED)
            } catch (e: Exception) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @DeleteMapping("/{semesterId}/courses/{courseId}")
    fun deleteCourseFromSemester(
        @PathVariable semesterId: UUID,
        @PathVariable courseId: UUID
    ): ResponseEntity<CourseResponse> {
        return try {
            val course = semesterService.deleteCourseFromSemester(semesterId, courseId)
            ResponseEntity(course, HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @GetMapping("/{id}/managers")
    fun getSemesterManagers(@PathVariable id: UUID): ResponseEntity<List<SemesterManagerDTO>> {
        return try {
            val managers = semesterService.getSemesterManagers(id)
            ResponseEntity.ok(managers)
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
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
        return try {
            val count = semesterService.getSemesterCount()
            ResponseEntity.ok(mapOf("count" to count))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }
}