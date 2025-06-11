package com.evalify.evalifybackend.course.controller
import com.evalify.evalifybackend.batch.domain.DTO.BatchResponse
import com.evalify.evalifybackend.core.exception.NotFoundException
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
        return try {
            val course = courseService.getCourseById(courseId)
            ResponseEntity.ok(course)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }
    @GetMapping("/{courseId}/instructors")
    fun getCourseInstructors(
        @PathVariable courseId: UUID
    ): ResponseEntity<List<UserResponse>> {
        return try {
            val instructors = courseService.getCourseInstructors(courseId)
            ResponseEntity.ok(instructors)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @GetMapping("/{courseId}/batches")
    fun getCourseBatches(
        @PathVariable courseId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<BatchResponse>> {
        return try {
            val batches = courseService.getCourseBatches(courseId, page, size, sort_by, sort_order)
            ResponseEntity.ok(batches)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @GetMapping("/{courseId}/students")
    fun getCourseStudents(
        @PathVariable courseId: UUID,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<UserResponse>> {
        return try {
            val students = courseService.getCourseStudents(courseId, page, size, sort_by, sort_order)
            ResponseEntity.ok(students)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }    @PutMapping("/{courseId}/assign-students")
    fun assignStudentsToCourses(@PathVariable courseId: UUID, @RequestBody studentIds: List<String>): ResponseEntity<Any> {
        return try {
            courseService.assignStudents(courseId, studentIds)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to assign students to course: ${e.message}"))
        }
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    fun removeStudentFromCourse(@PathVariable courseId: UUID, @PathVariable studentId: String): ResponseEntity<Any> {
        return try {
            courseService.removeStudents(courseId, listOf(studentId))
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to remove student from course: ${e.message}"))
        }
    }

    @PostMapping("/{courseId}/students")
    fun assignStudentsToCoursePost(
        @PathVariable courseId: UUID,
        @RequestBody requestBody: Map<String, List<String>>
    ): ResponseEntity<Any> {
        return try {
            val studentIds = requestBody["studentIds"] ?: emptyList()
            courseService.assignStudents(courseId, studentIds)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to assign students to course: ${e.message}"))        }
    }

    @PutMapping("/{courseId}/assign-instructor")
    fun assignInstructorsToCourses(@PathVariable courseId: UUID, @RequestBody userId: List<String>): ResponseEntity<Any> {
        return try {
            courseService.assignInstructors(courseId, userId)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to assign instructors to course: ${e.message}"))
        }
    }

    @PutMapping("/{courseId}/remove-instructor")
    fun removeInstructorsFromCourse(@PathVariable courseId: UUID, @RequestBody userId: List<String>): ResponseEntity<Any> {
        return try {
            courseService.removeInstructors(courseId, userId)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to remove instructors from course: ${e.message}"))
        }
    }

    @PostMapping("/{courseId}/batches")
    fun addBatchesToCoursePost(
        @PathVariable courseId: UUID,
        @RequestBody requestBody: Map<String, List<UUID>>
    ): ResponseEntity<Any> {
        return try {
            val batchIds = requestBody["batchIds"] ?: emptyList()
            courseService.addBatchesToCourse(courseId, batchIds)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to add batches to course: ${e.message}"))
        }
    }

    @DeleteMapping("/{courseId}/batches/{batchId}")
    fun removeBatchFromCourse(@PathVariable courseId: UUID, @PathVariable batchId: UUID): ResponseEntity<Any> {
        return try {
            courseService.removeBatchesFromCourse(courseId, listOf(batchId))
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to remove batch from course: ${e.message}"))
        }
    }    @PutMapping("/{courseId}/addBatch")
    fun addBatchToCourse(@PathVariable courseId: UUID, @RequestBody batchIds: List<UUID>): ResponseEntity<Any> {
        return try {
            courseService.addBatchesToCourse(courseId, batchIds)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to add batches to course: ${e.message}"))
        }
    }

    @PutMapping("/{courseId}/removeBatch")
    fun removeBatchesFromCourse(@PathVariable courseId: UUID, @RequestBody batchIds: List<UUID>): ResponseEntity<Any> {
        return try {
            courseService.removeBatchesFromCourse(courseId, batchIds)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to remove batches from course: ${e.message}"))
        }
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
        return try {
            val batches = courseService.searchCourseBatches(courseId, query, page, size, sort_by, sort_order)
            ResponseEntity.ok(batches)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
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
        return try {
            val students = courseService.searchCourseStudents(courseId, query, page, size, sort_by, sort_order)
            ResponseEntity.ok(students)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }    @GetMapping("/{courseId}/unassigned-students")
    fun getUnassignedStudents(
        @PathVariable courseId: UUID
    ): ResponseEntity<List<UserResponse>> {
        return try {
            val students = courseService.getUnassignedStudents(courseId)
            ResponseEntity.ok(students)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    @PostMapping("/{courseId}/instructors")
    fun assignInstructorsToCoursePost(
        @PathVariable courseId: UUID,
        @RequestBody requestBody: Map<String, List<String>>
    ): ResponseEntity<Any> {
        return try {
            val instructorIds = requestBody["instructorIds"] ?: emptyList()
            courseService.assignInstructors(courseId, instructorIds)
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to assign instructors to course: ${e.message}"))
        }
    }

    @DeleteMapping("/{courseId}/instructors/{instructorId}")
    fun removeInstructorFromCourse(@PathVariable courseId: UUID, @PathVariable instructorId: String): ResponseEntity<Any> {
        return try {
            courseService.removeInstructors(courseId, listOf(instructorId))
            ResponseEntity.ok().build()
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to remove instructor from course: ${e.message}"))
        }
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
        return try {
            val instructors = courseService.searchCourseInstructors(courseId, query, page, size, sort_by, sort_order)
            ResponseEntity.ok(instructors)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }
    @PostMapping("/my-courses")
    fun getMyActiveCourses(
        @RequestBody request: Map<String, Any>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            val userId = request["userId"].toString()


            val currentUser = userRepository.findById(userId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "User not found"))

            val courses = courseService.getCoursesForCurrentUser(currentUser, page, size, sort_by, sort_order)
            ResponseEntity.ok(courses)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to get courses: ${e.message}"))
        }
    }

    @PostMapping("/my-courses/search")
    fun searchMyActiveCourses(
        @RequestBody request: Map<String, Any>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            val userId = request["userId"].toString()
            val query = request["query"]?.toString()
                ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Query parameter is required"))

            val currentUser = userRepository.findById(userId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "User not found"))

            val courses = courseService.searchCoursesForCurrentUser(currentUser, query, page, size, sort_by, sort_order)
            ResponseEntity.ok(courses)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to search courses: ${e.message}"))
        }
    }

    @PostMapping("/active")
    fun getActiveCourses(
        @RequestBody request: Map<String, Any>,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            val userId = request["userId"].toString()

            val currentUser = userRepository.findById(userId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "User not found"))

            val courses = courseService.getActiveCoursesForCurrentUser(currentUser, page, size, sort_by, sort_order)
            ResponseEntity.ok(courses)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to get active courses: ${e.message}"))
        }
    }
}