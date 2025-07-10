package com.evalify.evalifybackend.course.controller
import com.evalify.evalifybackend.batch.domain.DTO.BatchResponse
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.course.domain.DTO.AssignUserDTO
import com.evalify.evalifybackend.course.domain.DTO.CourseResponse
import com.evalify.evalifybackend.course.domain.DTO.UpdateCourseBatchDTO
import com.evalify.evalifybackend.quiz.service.QuizCourseService
import com.evalify.evalifybackend.course.service.CourseService
import com.evalify.evalifybackend.user.domain.dto.UserResponse
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID


@RestController
@RequestMapping("/api/course")
class CourseController(
    private val courseService: CourseService,
    private val userRepository: UserRepository
) {
    @GetMapping("/{courseId}")
    fun getCourseById(@PathVariable courseId: UUID): ResponseEntity<CourseResponse> {
        val course = courseService.getCourseById(courseId)
        return ResponseEntity.ok(course)
    }
    @GetMapping("/{courseId}/instructors")
    fun getCourseInstructors(
        @PathVariable courseId: UUID
    ): ResponseEntity<List<UserResponse>> {
        val instructors = courseService.getCourseInstructors(courseId)
        return ResponseEntity.ok(instructors)
    }

    @GetMapping("/{courseId}/batches")
    fun getCourseBatches(
        @PathVariable courseId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<BatchResponse>> {
        val batches = courseService.getCourseBatches(courseId, page, size, sort_by, sort_order)
        return ResponseEntity.ok(batches)
    }

    @GetMapping("/{courseId}/students")
    fun getCourseStudents(
        @PathVariable courseId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<UserResponse>> {
        val students = courseService.getCourseStudents(courseId, page, size, sort_by, sort_order)
        return ResponseEntity.ok(students)
    }    @PutMapping("/{courseId}/assign-students")
    fun assignStudentsToCourses(@PathVariable courseId: UUID, @RequestBody studentIds: List<String>): ResponseEntity<Any> {
        courseService.assignStudents(courseId, studentIds)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    fun removeStudentFromCourse(@PathVariable courseId: UUID, @PathVariable studentId: String): ResponseEntity<Any> {
        courseService.removeStudents(courseId, listOf(studentId))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{courseId}/students")
    fun assignStudentsToCoursePost(
        @PathVariable courseId: UUID,
        @RequestBody requestBody: Map<String, List<String>>
    ): ResponseEntity<Any> {
        val studentIds = requestBody["studentIds"] ?: emptyList()
        courseService.assignStudents(courseId, studentIds)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{courseId}/assign-instructor")
    fun assignInstructorsToCourses(@PathVariable courseId: UUID, @RequestBody userId: List<String>): ResponseEntity<Any> {
        courseService.assignInstructors(courseId, userId)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{courseId}/remove-instructor")
    fun removeInstructorsFromCourse(@PathVariable courseId: UUID, @RequestBody userId: List<String>): ResponseEntity<Any> {
        courseService.removeInstructors(courseId, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{courseId}/batches")
    fun addBatchesToCoursePost(
        @PathVariable courseId: UUID,
        @RequestBody requestBody: Map<String, List<UUID>>
    ): ResponseEntity<Any> {
        val batchIds = requestBody["batchIds"] ?: emptyList()
        courseService.addBatchesToCourse(courseId, batchIds)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{courseId}/batches/{batchId}")
    fun removeBatchFromCourse(@PathVariable courseId: UUID, @PathVariable batchId: UUID): ResponseEntity<Any> {
        courseService.removeBatchesFromCourse(courseId, listOf(batchId))
        return ResponseEntity.ok().build()
    }    @PutMapping("/{courseId}/addBatch")
    fun addBatchToCourse(@PathVariable courseId: UUID, @RequestBody batchIds: List<UUID>): ResponseEntity<Any> {
        courseService.addBatchesToCourse(courseId, batchIds)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{courseId}/removeBatch")
    fun removeBatchesFromCourse(@PathVariable courseId: UUID, @RequestBody batchIds: List<UUID>): ResponseEntity<Any> {
        courseService.removeBatchesFromCourse(courseId, batchIds)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{courseId}/batches/search")
    fun searchCourseBatches(
        @PathVariable courseId: UUID,
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<BatchResponse>> {
        val batches = courseService.searchCourseBatches(courseId, query, page, size, sort_by, sort_order)
        return ResponseEntity.ok(batches)
    }

    @GetMapping("/{courseId}/students/search")
    fun searchCourseStudents(
        @PathVariable courseId: UUID,
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<UserResponse>> {
        val students = courseService.searchCourseStudents(courseId, query, page, size, sort_by, sort_order)
        return ResponseEntity.ok(students)
    }
    
    @GetMapping("/{courseId}/unassigned-students")
    fun getUnassignedStudents(
        @PathVariable courseId: UUID
    ): ResponseEntity<List<UserResponse>> {
        val students = courseService.getUnassignedStudents(courseId)
        return ResponseEntity.ok(students)
    }

    @PostMapping("/{courseId}/instructors")
    fun assignInstructorsToCoursePost(
        @PathVariable courseId: UUID,
        @RequestBody requestBody: Map<String, List<String>>
    ): ResponseEntity<Any> {
        val instructorIds = requestBody["instructorIds"] ?: emptyList()
        courseService.assignInstructors(courseId, instructorIds)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{courseId}/instructors/{instructorId}")
    fun removeInstructorFromCourse(@PathVariable courseId: UUID, @PathVariable instructorId: String): ResponseEntity<Any> {
        courseService.removeInstructors(courseId, listOf(instructorId))
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{courseId}/instructors/search")
    fun searchCourseInstructors(
        @PathVariable courseId: UUID,
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<UserResponse>> {
        val instructors = courseService.searchCourseInstructors(courseId, query, page, size, sort_by, sort_order)
        return ResponseEntity.ok(instructors)
    }
    @PostMapping("/my-courses")
    fun getMyActiveCourses(
        @RequestBody request: Map<String, Any>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        val userId = request["userId"].toString()

        val currentUser = userRepository.findById(userId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "User not found"))

        val courses = courseService.getCoursesForCurrentUser(currentUser, page, size, sort_by, sort_order)
        return ResponseEntity.ok(courses)
    }

    @PostMapping("/my-courses/search")
    fun searchMyActiveCourses(
        @RequestBody request: Map<String, Any>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        val userId = request["userId"].toString()
        val query = request["query"]?.toString()
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Query parameter is required"))

        val currentUser = userRepository.findById(userId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "User not found"))

        val courses = courseService.searchCoursesForCurrentUser(currentUser, query, page, size, sort_by, sort_order)
        return ResponseEntity.ok(courses)
    }

    @PostMapping("/active")
    fun getActiveCourses(
        @RequestBody request: Map<String, Any>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        val userId = request["userId"].toString()

        val currentUser = userRepository.findById(userId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "User not found"))

        val courses = courseService.getActiveCoursesForCurrentUser(currentUser, page, size, sort_by, sort_order)
        return ResponseEntity.ok(courses)
    }

    @GetMapping("/count")
    fun getCourseCount(): ResponseEntity<Map<String, Long>> {
        val count = courseService.getCourseCount()
        return ResponseEntity.ok(mapOf("count" to count))
    }
}