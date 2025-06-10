package com.evalify.evalifybackend.batch.service

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.batch.domain.DTO.BatchResponse
import com.evalify.evalifybackend.batch.domain.DTO.CreateBatchRequest
import com.evalify.evalifybackend.batch.domain.DTO.UpdateBatchRequest
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.department.domain.dto.DepartmentResponse
import com.evalify.evalifybackend.department.repository.DepartmentRepository
import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import com.evalify.evalifybackend.user.domain.dto.UserResponse
import com.evalify.evalifybackend.user.service.toUserResponse
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID


@Service
class BatchService(
    private val userRepository: UserRepository,
    private val batchRepository: BatchRepository,
    private val semesterRepository: SemesterRepository,
    private val departmentRepository: DepartmentRepository
) {    @Transactional
fun addStudentsToBatch(batchId: UUID, studentId: List<UUID>) {
    val batch = batchRepository.findById(batchId).orElseThrow {
        NotFoundException("Could not find course with id $batchId")
    }
    val users = userRepository.findAllById(studentId)
    batch.students.addAll(users)
    batchRepository.save(batch)
}    @Transactional
fun removeStudentsFromBatch(batchId: UUID, studentId: List<UUID>) {
    val batch = batchRepository.findById(batchId).orElseThrow {
        NotFoundException("Could not find course with id $batchId")
    }
    val users = userRepository.findAllById(studentId)
    batch.students.removeAll(users)
    batchRepository.save(batch)
}    @Transactional
fun addSemestersToBatches(batchId: UUID, semesterId: List<UUID>) {
    val batch = batchRepository.findById(batchId).orElseThrow {
        NotFoundException("Could not find course with id $batchId")
    }
    val semesters = semesterRepository.findAllById(semesterId)
    batch.semester.addAll(semesters)
    batchRepository.save(batch)
}    @Transactional
fun removeSemestersFromBatch(batchId: UUID, semesterId: List<UUID>) {
    val batch = batchRepository.findById(batchId).orElseThrow {
        NotFoundException("Could not find course with id $batchId")
    }
    val semesters = semesterRepository.findAllById(semesterId)
    batch.semester.removeAll(semesters)
    batchRepository.save(batch)
}    @Transactional
fun addManagersToBatch(batchId: UUID, managerId: List<UUID>) {
    val batch = batchRepository.findById(batchId).orElseThrow {
        NotFoundException("Could not find course with id $batchId")
    }
    val managers = userRepository.findAllById(managerId)
    batch.managers.addAll(managers)
    batchRepository.save(batch)
}    @Transactional
fun removeManagersFromBatch(batchId: UUID, managerId: List<UUID>) {
    val batch = batchRepository.findById(batchId).orElseThrow {
        NotFoundException("Could not find course with id $batchId")
    }
    val managers = userRepository.findAllById(managerId)
    batch.managers.removeAll(managers)
    batchRepository.save(batch)
}

    fun getAllBatches(): List<BatchResponse> {
        val batches = batchRepository.findAll()
        return batches.map { it.toBatchResponse() }
    }

    fun getAllBatches(page: Int = 0, size: Int = 10, sortBy: String? = "name", sortOrder: String? = "asc"): PaginatedResponse<BatchResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable: Pageable = PageRequest.of(page, size, sort)
        val batchesPage: Page<Batch> = batchRepository.findAll(pageable)

        return PaginatedResponse(
            data = batchesPage.content.map { it.toBatchResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = batchesPage.totalPages,
                total_count = batchesPage.totalElements.toInt()
            )
        )
    }

    fun searchBatches(query: String): List<BatchResponse> {
        return batchRepository.findByNameOrYearContainingIgnoreCase(query).map{
                batch -> batch.toBatchResponse()
        }
    }

    // Search batches with pagination
    fun searchBatches(
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String? = null,
        sortOrder: String? = null
    ): PaginatedResponse<BatchResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable: Pageable = PageRequest.of(page, size, sort)
        val batchesPage: Page<Batch> = batchRepository.searchByNameOrYearContainingIgnoreCase(query, pageable)

        return PaginatedResponse(
            data = batchesPage.content.map { it.toBatchResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = batchesPage.totalPages,
                total_count = batchesPage.totalElements.toInt()
            )
        )
    }

    @Transactional(readOnly = true)
    fun getActiveSemester(batchId: UUID): Semester? {
        val batch = batchRepository.findById(batchId).orElseThrow {
            NotFoundException("Could not find batch with id $batchId")
        }
        return batch.semester.find { it.isActive }    }

    @Transactional
    fun createBatch(request: CreateBatchRequest): BatchResponse {
        val department = request.departmentId?.let { departmentId ->
            departmentRepository.findById(departmentId).orElseThrow {
                NotFoundException("Department with id $departmentId not found")
            }
        }

        val batch = Batch(
            name = request.name,
            graduationYear = request.graduationYear,
            section = request.section,
            isActive = request.isActive,
            department = department
        )

        val savedBatch = batchRepository.save(batch)
        return savedBatch.toBatchResponse()    }

    @Transactional
    fun updateBatch(batchId: UUID, request: UpdateBatchRequest): BatchResponse {
        val batch = batchRepository.findById(batchId).orElseThrow {
            NotFoundException("Batch with id $batchId not found")
        }

        request.name?.let { batch.name = it }
        request.graduationYear?.let { batch.graduationYear = it }
        request.section?.let { batch.section = it }
        request.isActive?.let { batch.isActive = it }
        request.departmentId?.let { departmentId ->
            val department = departmentRepository.findById(departmentId).orElseThrow {
                NotFoundException("Department with id $departmentId not found")
            }
            batch.department = department
        }

        val savedBatch = batchRepository.save(batch)
        return savedBatch.toBatchResponse()
    }    @Transactional(readOnly = true)
    fun getBatchStudents(
        batchId: UUID,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<UserResponse> {
        val batch = batchRepository.findById(batchId).orElseThrow {
            NotFoundException("Batch with id $batchId not found")
        }

        // Force initialization of students collection to avoid LazyInitializationException
        batch.students.size

        // Apply sorting and pagination manually since we can't use repository methods for this collection
        val direction = if (sortOrder.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        // Sort the students collection manually
        val sortedStudents = when (sortBy.lowercase()) {
            "name" -> if (direction == Sort.Direction.ASC)
                batch.students.sortedBy { it.name }
            else
                batch.students.sortedByDescending { it.name }
            "email" -> if (direction == Sort.Direction.ASC)
                batch.students.sortedBy { it.email }
            else
                batch.students.sortedByDescending { it.email }
            "createdat" -> if (direction == Sort.Direction.ASC)
                batch.students.sortedBy { it.createdAt }
            else
                batch.students.sortedByDescending { it.createdAt }
            else -> batch.students.sortedBy { it.name } // Default sort by name
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
    fun getBatchById(batchId: UUID): BatchResponse {
        val batch = batchRepository.findById(batchId).orElseThrow {
            NotFoundException("Batch with id $batchId not found")
        }
        return batch.toBatchResponse()
    }

    @Transactional(readOnly = true)
    fun searchBatchStudents(
        batchId: UUID,
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String = "name",
        sortOrder: String = "asc"
    ): PaginatedResponse<UserResponse> {
        val batch = batchRepository.findById(batchId).orElseThrow {
            NotFoundException("Batch with id $batchId not found")
        }

        // Force initialization of students collection to avoid LazyInitializationException
        batch.students.size

        // Filter students by name or email containing the query
        val filteredStudents = batch.students.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true)
        }

        // Apply sorting and pagination manually since we can't use repository methods for this collection
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

    private fun createSort(sortBy: String?, sortOrder: String?): Sort {
        return if (sortBy != null) {
            val direction = if (sortOrder?.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC
            Sort.by(direction, sortBy)
        } else {
            Sort.by(Sort.Direction.ASC, "name") // Default sort by name
        }
    }
}

fun Batch.toBatchResponse(): BatchResponse {
    return BatchResponse(
        id = this.id,
        name = this.name,
        graduationYear = this.graduationYear,
        section = this.section,
        isActive = this.isActive,
        department = this.department?.let {
            DepartmentResponse(
                id = it.id.toString(),
                name = it.name
            )
        }
    )
}
