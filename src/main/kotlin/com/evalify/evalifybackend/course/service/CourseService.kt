package com.evalify.evalifybackend.course.service
import com.evalify.evalifybackend.batch.domain.DTO.BatchResponse
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.batch.service.toBatchResponse
import com.evalify.evalifybackend.course.exception.CourseNotFoundException
import com.evalify.evalifybackend.course.exception.UserNotFoundException
import com.evalify.evalifybackend.course.exception.UsersNotFoundException
import com.evalify.evalifybackend.course.exception.BatchNotFoundException
import com.evalify.evalifybackend.course.exception.BatchesNotFoundException
import com.evalify.evalifybackend.course.exception.CourseStudentException
import com.evalify.evalifybackend.course.exception.CourseInstructorException
import com.evalify.evalifybackend.course.exception.CourseBatchException
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.course.domain.DTO.CourseResponse
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.user.domain.Role
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.domain.dto.UserResponse
import com.evalify.evalifybackend.user.service.toUserResponse
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.collections.addAll
import kotlin.collections.removeAll
import kotlin.div
import kotlin.text.toInt

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val batchRepository: BatchRepository
) {
    @Transactional
    fun assignStudents(courseId: UUID, studentId: List<String>) {
        // Validate input
        if (studentId.isEmpty()) {
            throw CourseStudentException("Student ID list cannot be empty")
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        
        // Force initialization of students collection to avoid LazyInitializationException
        course.students.size
        
        // Validate all student IDs exist
        val users = userRepository.findAllById(studentId)
        val foundUserIds = users.map { it.id }
        val missingUserIds = studentId.filter { !foundUserIds.contains(it) }
        
        if (missingUserIds.isNotEmpty()) {
            throw UsersNotFoundException(missingUserIds)
        }
        
        // Check if any students are already assigned
        val alreadyAssignedUsers = users.filter { user ->
            course.students.any { it.id == user.id }
        }
        
        if (alreadyAssignedUsers.isNotEmpty()) {
            throw CourseStudentException("Students ${alreadyAssignedUsers.map { it.name }.joinToString(", ")} are already assigned to this course")
        }
        
        course.students.addAll(users)
        courseRepository.save(course)
    }

    @Transactional
    fun removeStudents(courseId: UUID, studentId: List<String>) {
        // Validate input
        if (studentId.isEmpty()) {
            throw CourseStudentException("Student ID list cannot be empty")
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        
        // Force initialization of students collection to avoid LazyInitializationException
        course.students.size
        
        // Validate all student IDs exist
        val users = userRepository.findAllById(studentId)
        val foundUserIds = users.map { it.id }
        val missingUserIds = studentId.filter { !foundUserIds.contains(it) }
        
        if (missingUserIds.isNotEmpty()) {
            throw UsersNotFoundException(missingUserIds)
        }
        
        course.students.removeAll(users)
        courseRepository.save(course)
    }

    @Transactional
    fun assignInstructors(courseId: UUID, instructorId: List<String>) {
        // Validate input
        if (instructorId.isEmpty()) {
            throw CourseInstructorException("Instructor ID list cannot be empty")
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        
        // Force initialization of instructors collection to avoid LazyInitializationException
        course.instructors.size
        
        // Validate all instructor IDs exist
        val users = userRepository.findAllById(instructorId)
        val foundUserIds = users.map { it.id }
        val missingUserIds = instructorId.filter { !foundUserIds.contains(it) }
        
        if (missingUserIds.isNotEmpty()) {
            throw UsersNotFoundException(missingUserIds)
        }
        
        // Check if any instructors are already assigned
        val alreadyAssignedUsers = users.filter { user ->
            course.instructors.any { it.id == user.id }
        }
        
        if (alreadyAssignedUsers.isNotEmpty()) {
            throw CourseInstructorException("Instructors ${alreadyAssignedUsers.map { it.name }.joinToString(", ")} are already assigned to this course")
        }
        
        course.instructors.addAll(users)
        courseRepository.save(course)
    }

    @Transactional
    fun removeInstructors(courseId: UUID, instructorId: List<String>) {
        // Validate input
        if (instructorId.isEmpty()) {
            throw CourseInstructorException("Instructor ID list cannot be empty")
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        
        // Force initialization of instructors collection to avoid LazyInitializationException
        course.instructors.size
        
        // Validate all instructor IDs exist
        val users = userRepository.findAllById(instructorId)
        val foundUserIds = users.map { it.id }
        val missingUserIds = instructorId.filter { !foundUserIds.contains(it) }
        
        if (missingUserIds.isNotEmpty()) {
            throw UsersNotFoundException(missingUserIds)
        }
        
        course.instructors.removeAll(users)
        courseRepository.save(course)
    }

    @Transactional
    fun addBatchesToCourse(courseId: UUID, batchId: List<UUID>) {
        // Validate input
        if (batchId.isEmpty()) {
            throw CourseBatchException("Batch ID list cannot be empty")
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        
        // Force initialization of batches collection to avoid LazyInitializationException
        course.batches.size
        
        // Validate all batch IDs exist
        val batches = batchRepository.findAllById(batchId)
        val foundBatchIds = batches.map { it.id }
        val missingBatchIds = batchId.filter { !foundBatchIds.contains(it) }
        
        if (missingBatchIds.isNotEmpty()) {
            throw BatchesNotFoundException(missingBatchIds)
        }
        
        // Check if any batches are already assigned
        val alreadyAssignedBatches = batches.filter { batch ->
            course.batches.any { it.id == batch.id }
        }
        
        if (alreadyAssignedBatches.isNotEmpty()) {
            throw CourseBatchException("Batches ${alreadyAssignedBatches.map { it.name }.joinToString(", ")} are already assigned to this course")
        }
        
        course.batches.addAll(batches)
        courseRepository.save(course)
    }

    @Transactional
    fun removeBatchesFromCourse(courseId: UUID, batchId: List<UUID>) {
        // Validate input
        if (batchId.isEmpty()) {
            throw CourseBatchException("Batch ID list cannot be empty")
        }

        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        
        // Force initialization of batches collection to avoid LazyInitializationException
        course.batches.size
        
        // Validate all batch IDs exist
        val batches = batchRepository.findAllById(batchId)
        val foundBatchIds = batches.map { it.id }
        val missingBatchIds = batchId.filter { !foundBatchIds.contains(it) }
        
        if (missingBatchIds.isNotEmpty()) {
            throw BatchesNotFoundException(missingBatchIds)
        }
        
        course.batches.removeAll(batches)
        courseRepository.save(course)
    }

    @Transactional(readOnly = true)
    fun getCourseById(courseId: UUID): CourseResponse {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }
        return course.toCourseResponse()
    }

    @Transactional(readOnly = true)
    fun getCourseBatches(
        courseId: UUID,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<BatchResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of batches collection to avoid LazyInitializationException
        course.batches.size

        // Apply sorting manually since we can't use repository methods for this collection
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        // Sort the batches collection manually
        val sortedBatches = when (sortBy.lowercase()) {
            "name" -> if (direction == Sort.Direction.ASC)
                course.batches.sortedBy { it.name }
            else
                course.batches.sortedByDescending { it.name }
            "graduationyear" -> if (direction == Sort.Direction.ASC)
                course.batches.sortedBy { it.graduationYear }
            else
                course.batches.sortedByDescending { it.graduationYear }
            "section" -> if (direction == Sort.Direction.ASC)
                course.batches.sortedBy { it.section }
            else
                course.batches.sortedByDescending { it.section }
            else -> course.batches.sortedBy { it.name } // Default sort by name
        }

        // Apply pagination
        val totalElements = sortedBatches.size
        val totalPages = (totalElements + size - 1) / size // Ceiling division
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, totalElements)

        val pagedBatches = if (startIndex < totalElements) {
            sortedBatches.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            data = pagedBatches.map { it.toBatchResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = totalPages,
                total_count = totalElements
            )
        )
    }

    @Transactional(readOnly = true)
    fun getCourseStudents(
        courseId: UUID,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<UserResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of students collection to avoid LazyInitializationException
        course.students.size

        // Filter only STUDENT role users
        val studentUsers = course.students.filter { it.role == Role.STUDENT }

        // Apply sorting manually since we can't use repository methods for this collection
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        // Sort the students collection manually
        val sortedStudents = when (sortBy.lowercase()) {
            "name" -> if (direction == Sort.Direction.ASC)
                studentUsers.sortedBy { it.name }
            else
                studentUsers.sortedByDescending { it.name }
            "email" -> if (direction == Sort.Direction.ASC)
                studentUsers.sortedBy { it.email }
            else
                studentUsers.sortedByDescending { it.email }
            "createdat" -> if (direction == Sort.Direction.ASC)
                studentUsers.sortedBy { it.createdAt }
            else
                studentUsers.sortedByDescending { it.createdAt }
            else -> studentUsers.sortedBy { it.name } // Default sort by name
        }

        // Apply pagination
        val totalElements = sortedStudents.size
        val totalPages = (totalElements + size - 1) / size // Ceiling division
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, totalElements)

        val pagedStudents = if (startIndex < totalElements) {
            sortedStudents.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            data = pagedStudents.map { it.toUserResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = totalPages,
                total_count = totalElements
            )
        )
    }

    @Transactional(readOnly = true)
    fun searchCourseBatches(
        courseId: UUID,
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<BatchResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of batches collection to avoid LazyInitializationException
        course.batches.size

        // Filter batches by name or section containing the query
        val filteredBatches = course.batches.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.section.contains(query, ignoreCase = true) ||
                    it.graduationYear.toString().contains(query, ignoreCase = true)
        }

        // Apply sorting and pagination manually
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        // Sort the filtered batches collection manually
        val sortedBatches = when (sortBy.lowercase()) {
            "name" -> if (direction == Sort.Direction.ASC)
                filteredBatches.sortedBy { it.name }
            else
                filteredBatches.sortedByDescending { it.name }
            "graduationyear" -> if (direction == Sort.Direction.ASC)
                filteredBatches.sortedBy { it.graduationYear }
            else
                filteredBatches.sortedByDescending { it.graduationYear }
            "section" -> if (direction == Sort.Direction.ASC)
                filteredBatches.sortedBy { it.section }
            else
                filteredBatches.sortedByDescending { it.section }
            else -> filteredBatches.sortedBy { it.name } // Default sort by name
        }

        // Apply pagination
        val totalElements = sortedBatches.size
        val totalPages = (totalElements + size - 1) / size // Ceiling division
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, totalElements)

        val pagedBatches = if (startIndex < totalElements) {
            sortedBatches.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            data = pagedBatches.map { it.toBatchResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = totalPages,
                total_count = totalElements
            )
        )
    }

    @Transactional(readOnly = true)
    fun searchCourseStudents(
        courseId: UUID,
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<UserResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of students collection to avoid LazyInitializationException
        course.students.size

        // Filter students by name or email containing the query
        val filteredStudents = course.students.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.profileId?.contains(query, ignoreCase = true) == true
        }

        // Apply sorting and pagination manually
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        // Sort the filtered students collection manually
        val sortedStudents = when (sortBy.lowercase()) {
            "name" -> if (direction == Sort.Direction.ASC)
                filteredStudents.sortedBy { it.name }
            else
                filteredStudents.sortedByDescending { it.name }
            "email" -> if (direction == Sort.Direction.ASC)
                filteredStudents.sortedBy { it.email }
            else
                filteredStudents.sortedByDescending { it.email }
            "createdat" -> if (direction == Sort.Direction.ASC)
                filteredStudents.sortedBy { it.createdAt }
            else
                filteredStudents.sortedByDescending { it.createdAt }
            else -> filteredStudents.sortedBy { it.name } // Default sort by name
        }

        // Apply pagination
        val totalElements = sortedStudents.size
        val totalPages = (totalElements + size - 1) / size // Ceiling division
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, totalElements)

        val pagedStudents = if (startIndex < totalElements) {
            sortedStudents.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            data = pagedStudents.map { it.toUserResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = totalPages,
                total_count = totalElements
            )
        )
    }

    // Non-paginated version to be used by the instructor endpoint
    @Transactional(readOnly = true)
    fun getCourseInstructors(
        courseId: UUID
    ): List<UserResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of instructors collection to avoid LazyInitializationException
        course.instructors.size

        // Return all instructors without pagination
        return course.instructors.map { it.toUserResponse() }
    }

    @Transactional(readOnly = true)
    fun searchCourseInstructors(
        courseId: UUID,
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<UserResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of instructors collection to avoid LazyInitializationException
        course.instructors.size

        // Filter instructors by name or email containing the query
        val filteredInstructors = course.instructors.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.profileId?.contains(query, ignoreCase = true) == true
        }

        // Apply sorting and pagination manually
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        // Sort the filtered instructors collection manually
        val sortedInstructors = when (sortBy.lowercase()) {
            "name" -> if (direction == Sort.Direction.ASC)
                filteredInstructors.sortedBy { it.name }
            else
                filteredInstructors.sortedByDescending { it.name }
            "email" -> if (direction == Sort.Direction.ASC)
                filteredInstructors.sortedBy { it.email }
            else
                filteredInstructors.sortedByDescending { it.email }
            "createdat" -> if (direction == Sort.Direction.ASC)
                filteredInstructors.sortedBy { it.createdAt }
            else
                filteredInstructors.sortedByDescending { it.createdAt }
            else -> filteredInstructors.sortedBy { it.name } // Default sort by name
        }

        // Apply pagination
        val totalElements = sortedInstructors.size
        val totalPages = (totalElements + size - 1) / size // Ceiling division
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, totalElements)

        val pagedInstructors = if (startIndex < totalElements) {
            sortedInstructors.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return PaginatedResponse(
            data = pagedInstructors.map { it.toUserResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = totalPages,
                total_count = totalElements
            )
        )
    }
    @Transactional(readOnly = true)
    fun getUnassignedStudents(courseId: UUID): List<UserResponse> {
        val course = courseRepository.findById(courseId).orElseThrow {
            CourseNotFoundException(courseId)
        }

        // Force initialization of students collection to avoid LazyInitializationException
        course.students.size

        // Get all users with STUDENT role who are not in the course
        val allStudentUsers = userRepository.findByRole(Role.STUDENT)
        val unassignedStudents = allStudentUsers.filter { student ->
            !course.students.map { it.id }.contains(student.id)
        }

        return unassignedStudents.map { it.toUserResponse() }
    }

    @Transactional(readOnly = true)
    fun getCoursesForCurrentUser(
        currentUser: User,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<CourseResponse> {
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC
        val sort = Sort.by(direction, sortBy)
        val pageable = PageRequest.of(page, size, sort)

        val coursePage = when (currentUser.role) {
            Role.MANAGER -> {
                // Managers can see all courses from active semesters
                courseRepository.findCoursesByActiveSemesters(pageable)
            }
            Role.FACULTY -> {
                // Faculty can only see courses where they are instructors in active semesters
                courseRepository.findCoursesByActiveSemestersAndInstructor(currentUser, pageable)
            }
            else -> {
                throw IllegalArgumentException("Access denied. Only MANAGER and FACULTY roles can access this endpoint.")
            }
        }

        return PaginatedResponse(
            data = coursePage.content.map { it.toCourseResponse() },
            pagination = PaginationInfo(
                current_page = coursePage.number,
                per_page = coursePage.size,
                total_pages = coursePage.totalPages,
                total_count = coursePage.totalElements.toInt()
            )
        )
    }

    @Transactional(readOnly = true)
    fun searchCoursesForCurrentUser(
        currentUser: User,
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<CourseResponse> {
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC
        val sort = Sort.by(direction, sortBy)
        val pageable = PageRequest.of(page, size, sort)

        val coursePage = when (currentUser.role) {
            Role.MANAGER -> {
                // Managers can search all courses from active semesters
                courseRepository.searchCoursesByActiveSemesters(query, pageable)
            }
            Role.FACULTY -> {
                // Faculty can only search courses where they are instructors in active semesters
                courseRepository.searchCoursesByActiveSemestersAndInstructor(currentUser, query, pageable)
            }
            else -> {
                throw IllegalArgumentException("Access denied. Only MANAGER and FACULTY roles can access this endpoint.")
            }
        }

        return PaginatedResponse(
            data = coursePage.content.map { it.toCourseResponse() },
            pagination = PaginationInfo(
                current_page = coursePage.number,
                per_page = coursePage.size,
                total_pages = coursePage.totalPages,
                total_count = coursePage.totalElements.toInt()
            )
        )
    }

    @Transactional(readOnly = true)
    fun getActiveCoursesForCurrentUser(
        currentUser: User,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<CourseResponse> {
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC
        val sort = Sort.by(direction, sortBy)
        val pageable = PageRequest.of(page, size, sort)

        val coursePage = when (currentUser.role) {
            Role.STUDENT -> {
                // Students get courses they're assigned to in active semesters
                courseRepository.findCoursesByActiveSemestersAndStudent(currentUser, pageable)
            }
            Role.FACULTY -> {
                // Faculty get courses they're assigned to in active semesters
                courseRepository.findCoursesByActiveSemestersAndInstructor(currentUser, pageable)
            }
            Role.ADMIN, Role.MANAGER -> {
                // Admin/Manager get all courses in active semesters
                courseRepository.findCoursesByActiveSemesters(pageable)
            }
        }

        return PaginatedResponse(
            data = coursePage.content.map { it.toCourseResponse() },
            pagination = PaginationInfo(
                current_page = coursePage.number,
                per_page = coursePage.size,
                total_pages = coursePage.totalPages,
                total_count = coursePage.totalElements.toInt()
            )
        )
    }

    fun getCourseCount(): Long{
        return courseRepository.count()
    }
}

fun Course.toCourseResponse(): CourseResponse {
    return CourseResponse(
        id = this.id!!,
        name = this.name,
        code = this.code,
        description = this.description
    )
}