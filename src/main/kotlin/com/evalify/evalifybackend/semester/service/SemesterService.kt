package com.evalify.evalifybackend.semester.service
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.course.domain.DTO.CourseResponse
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.semester.domain.DTO.SemesterManagerDTO
import com.evalify.evalifybackend.semester.domain.DTO.SemesterRequest
import com.evalify.evalifybackend.semester.domain.DTO.SemesterResponse
import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.semester.exception.*
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.code
import kotlin.collections.addAll
import kotlin.collections.removeAll

@Service
@Transactional
class SemesterService
    (
    val semesterRepository: SemesterRepository,
    val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val batchRepository: BatchRepository
) {
    fun assignManagersToSemester(semesterId: UUID, managersId: List<String>) {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        
        if (managersId.isEmpty()) {
            throw SemesterValidationException("Manager IDs list cannot be empty")
        }
        
        val managers = userRepository.findAllById(managersId)
        if (managers.size != managersId.size) {
            throw ManagersNotFoundException("Some managers could not be found")
        }
        
        semester.managers.addAll(managers)
        semesterRepository.save(semester)
    }

    fun createSemester(semesterRequest: SemesterRequest): SemesterResponse {
        // Validate semester request
        if (semesterRequest.name.isBlank()) {
            throw SemesterValidationException("Semester name cannot be blank")
        }
        if (semesterRequest.year < 1900 || semesterRequest.year > 2100) {
            throw SemesterValidationException("Semester year must be between 1900 and 2100")
        }
        
        try {
            val semester = Semester(
                name = semesterRequest.name,
                year = semesterRequest.year,
                isActive = semesterRequest.isActive
            )
            val savedSemester = semesterRepository.save(semester)
            return savedSemester.toSemesterResponse()
        } catch (e: Exception) {
            when (e) {
                is SemesterValidationException -> throw e
                else -> throw SemesterServiceException("Failed to create semester", e)
            }
        }
    }

    fun updateSemester(semesterId: UUID, semesterRequest: SemesterRequest): SemesterResponse {
        // Validate semester request
        if (semesterRequest.name.isBlank()) {
            throw SemesterValidationException("Semester name cannot be blank")
        }
        if (semesterRequest.year < 1900 || semesterRequest.year > 2100) {
            throw SemesterValidationException("Semester year must be between 1900 and 2100")
        }
        
        try {
            val semester = semesterRepository.findById(semesterId).orElseThrow {
                SemesterNotFoundException(semesterId.toString())
            }
            val newSemester = Semester(
                id = semester.id,
                name = semesterRequest.name,
                year = semesterRequest.year,
                isActive = semesterRequest.isActive
            )
            val updatedSemester = semesterRepository.save(newSemester)
            return updatedSemester.toSemesterResponse()
        } catch (e: Exception) {
            when (e) {
                is SemesterNotFoundException, is SemesterValidationException -> throw e
                else -> throw SemesterServiceException("Failed to update semester", e)
            }
        }
    }

    fun deleteSemester(semesterId: UUID) {
        try {
            val semester = semesterRepository.findById(semesterId).orElseThrow {
                SemesterNotFoundException(semesterId.toString())
            }
            semesterRepository.delete(semester)
        } catch (e: Exception) {
            when (e) {
                is SemesterNotFoundException -> throw e
                else -> throw SemesterServiceException("Failed to delete semester", e)
            }
        }
    }

    fun removeManagersFromSemester(semesterId: UUID, managersId: List<String>) {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        
        if (managersId.isEmpty()) {
            throw SemesterValidationException("Manager IDs list cannot be empty")
        }
        
        val managers = userRepository.findAllById(managersId)
        if (managers.size != managersId.size) {
            throw ManagersNotFoundException("Some managers could not be found")
        }
        semester.managers.removeAll(managers)
        semesterRepository.save(semester)
    }

    fun addCourseToSemester(semesterId: UUID, courseId: List<UUID>) {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        
        if (courseId.isEmpty()) {
            throw SemesterValidationException("Course IDs list cannot be empty")
        }
        
        val courses = courseRepository.findAllById(courseId)
        if (courses.size != courseId.size) {
            throw CoursesNotFoundException("Some courses could not be found")
        }
        
        semester.courses.addAll(courses)
        semesterRepository.save(semester)
    }

    fun removeCourseFromSemester(semesterId: UUID, courseId: List<UUID>) {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        
        if (courseId.isEmpty()) {
            throw SemesterValidationException("Course IDs list cannot be empty")
        }
        
        val courses = courseRepository.findAllById(courseId)
        if (courses.size != courseId.size) {
            throw CoursesNotFoundException("Some courses could not be found")
        }
        
        semester.courses.removeAll(courses)
        semesterRepository.save(semester)
    }

    fun getAllSemestersPaginated(page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<SemesterResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable = PageRequest.of(page, size, sort)
        val semesterPage: Page<Semester> = semesterRepository.findAll(pageable)

        return PaginatedResponse(
            data = semesterPage.content.map { it.toSemesterResponse() },
            pagination = PaginationInfo(
                current_page = page + 1,
                per_page = size,
                total_pages = semesterPage.totalPages,
                total_count = semesterPage.totalElements.toInt()
            )
        )
    }

    fun searchSemesterPaginated(query: String, page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<SemesterResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable = PageRequest.of(page, size, sort)
        val semesterPage: Page<Semester> = semesterRepository.findByNameOrYearContainingIgnoreCasePage(query, pageable)

        return PaginatedResponse(
            data = semesterPage.content.map { it.toSemesterResponse() },
            pagination = PaginationInfo(
                current_page = page + 1,
                per_page = size,
                total_pages = semesterPage.totalPages,
                total_count = semesterPage.totalElements.toInt()
            )
        )
    }

    private fun createSort(sortBy: String, sortOrder: String): Sort {
        val direction = if (sortOrder.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        return Sort.by(direction, sortBy)
    }

    // Legacy methods for backwards compatibility
    fun getAllSemester(): List<SemesterResponse> {
        return semesterRepository.findAll().map { it.toSemesterResponse() }
    }

    fun searchSemester(query: String): List<SemesterResponse> {
        return semesterRepository.findByNameOrYearContainingIgnoreCase(query).map { user -> user.toSemesterResponse() }
    }

    fun getSemesterById(semesterId: UUID): SemesterResponse {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        return semester.toSemesterResponse()
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    fun getCoursesBySemesterId(semesterId: UUID): List<CourseResponse> {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        // Force initialization of the courses collection to avoid LazyInitializationException
        semester.courses.size

        return semester.courses.map { course ->
            CourseResponse(
                id = course.id!!,
                name = course.name,
                code = course.code,
                description = course.description
            )
        }
    }

    fun createCourseForSemester(semesterId: UUID, courseRequest: com.evalify.evalifybackend.course.domain.DTO.CreateCourseRequest): CourseResponse {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }
        
        // Validate course request
        if (courseRequest.name.isBlank()) {
            throw SemesterValidationException("Course name cannot be blank")
        }
        if (courseRequest.code.isBlank()) {
            throw SemesterValidationException("Course code cannot be blank")
        }
        
        try {
            // Do not set review at all
            val course = com.evalify.evalifybackend.course.domain.Course(
                name = courseRequest.name,
                code = courseRequest.code,
                description = courseRequest.description,
                type = courseRequest.type,
                semester = semester
                // review is not set here
            )
            val savedCourse = courseRepository.save(course)
            return CourseResponse(
                id = savedCourse.id!!,
                name = savedCourse.name,
                code = savedCourse.code,
                description = savedCourse.description
            )
        } catch (e: Exception) {
            when (e) {
                is SemesterNotFoundException, is SemesterValidationException -> throw e
                else -> throw SemesterServiceException("Failed to create course for semester", e)
            }
        }
    }

    fun deleteCourseFromSemester(semesterId: UUID, courseId: UUID): CourseResponse {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            SemesterNotFoundException(semesterId.toString())
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CoursesNotFoundException("Course with id $courseId not found")
        }

        if (course.semester.id != semester.id) {
            throw CourseAssignmentException("Course with id $courseId does not belong to semester with id $semesterId")
        }

        val courseResponse = CourseResponse(
            id = course.id!!,
            name = course.name,
            code = course.code,
            description = course.description
        )

        courseRepository.delete(course)
        return courseResponse
    }

    fun getSemesterCount():Long {
        return semesterRepository.count()
    }

    fun getSemesterManagers(id: UUID): List<SemesterManagerDTO> {
        val semester = semesterRepository.findById(id).orElseThrow {
            SemesterNotFoundException(id.toString())
        }

        return semester.managers.map {
            SemesterManagerDTO(
                name = it.name,
                id = it.id,
                profileId = it.profileId
            )
        }
    }

}

fun Semester.toSemesterResponse(): SemesterResponse {
    return SemesterResponse(
        id = this.id!!,
        name = this.name,
        year = this.year,
        isActive = this.isActive
    )
}