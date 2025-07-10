package com.evalify.evalifybackend.department.service

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.department.exception.DepartmentNotFoundException
import com.evalify.evalifybackend.department.exception.DepartmentAlreadyExistsException
import com.evalify.evalifybackend.department.exception.DepartmentValidationException
import com.evalify.evalifybackend.department.exception.DepartmentServiceException
import com.evalify.evalifybackend.department.exception.DepartmentDeleteException
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.department.domain.Department
import com.evalify.evalifybackend.department.domain.dto.CreateDepartmentRequest
import com.evalify.evalifybackend.department.domain.dto.DepartmentBatchResponse
import com.evalify.evalifybackend.department.domain.dto.DepartmentResponse
import com.evalify.evalifybackend.department.domain.dto.UpdateDepartmentRequest
import com.evalify.evalifybackend.department.repository.DepartmentRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class DepartmentService(
    private val departmentRepository: DepartmentRepository
) {    @Transactional(readOnly = true)
    fun getAllDepartments(
        page: Int = 0, 
        size: Int = 10, 
        sortBy: String? = null, 
        sortOrder: String? = null
    ): PaginatedResponse<DepartmentResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable: Pageable = PageRequest.of(page, size, sort)
        
        // First get paged department IDs
        val departmentIdsPage = departmentRepository.findAllDepartmentIds(pageable)
        
        // Then fetch all those departments with their batches in a single query
        val departmentIds = departmentIdsPage.content
        val departments = if (departmentIds.isNotEmpty()) {
            departmentRepository.findAllById(departmentIds).map { department ->
                // Force initialization of batches collection
                department.batches.size
                department
            }
        } else {
            emptyList()
        }
        
        return PaginatedResponse(
            data = departments.map { it.toDepartmentResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = departmentIdsPage.totalPages,
                total_count = departmentIdsPage.totalElements.toInt()
            )
        )
    }    // Search departments with pagination
    @Transactional(readOnly = true)
    fun searchDepartments(
        query: String,
        page: Int = 0,
        size: Int = 10,
        sortBy: String? = null,
        sortOrder: String? = null
    ): PaginatedResponse<DepartmentResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable: Pageable = PageRequest.of(page, size, sort)
        
        // First get paged department IDs matching the search
        val departmentIdsPage = departmentRepository.findDepartmentIdsByNameContainingIgnoreCase(query, pageable)
        
        // Then fetch all those departments with their batches in a single query
        val departmentIds = departmentIdsPage.content
        val departments = if (departmentIds.isNotEmpty()) {
            departmentRepository.findAllById(departmentIds).map { department ->
                // Force initialization of batches collection
                department.batches.size
                department
            }
        } else {
            emptyList()
        }
        
        return PaginatedResponse(
            data = departments.map { it.toDepartmentResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = departmentIdsPage.totalPages,
                total_count = departmentIdsPage.totalElements.toInt()
            )
        )
    }
    
    private fun createSort(sortBy: String?, sortOrder: String?): Sort {
        return if (sortBy != null) {
            val direction = if (sortOrder?.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC
            Sort.by(direction, sortBy)
        } else {
            Sort.by(Sort.Direction.ASC, "name") // Default sort by name
        }    }

    // Legacy method - get all departments without pagination
    @Transactional(readOnly = true)
    fun getAllDepartments(): List<Department>{
        return departmentRepository.findAll()
    }

    fun createDepartment(request: CreateDepartmentRequest): Department {
        // Validate input
        if (request.name.isBlank()) {
            throw DepartmentValidationException("Department name cannot be empty")
        }
        
        // Check if department with this name already exists
        val existingDepartment = departmentRepository.findByNameIgnoreCase(request.name.trim())
        if (existingDepartment != null) {
            throw DepartmentAlreadyExistsException(request.name.trim())
        }
        
        try {
            val department = Department(name = request.name.trim())
            val savedDepartment = departmentRepository.save(department)
            // Force initialization of batches collection (though it will be empty for new departments)
            savedDepartment.batches.size
            return savedDepartment
        } catch (e: Exception) {
            throw DepartmentServiceException("Failed to create department: ${e.message}")
        }
    }    fun updateDepartment(departmentId: UUID, request: UpdateDepartmentRequest): Department {
        val department = departmentRepository.findById(departmentId).orElseThrow {
            DepartmentNotFoundException(departmentId)
        }
        
        // Validate name if provided
        request.name?.let { newName ->
            if (newName.isBlank()) {
                throw DepartmentValidationException("Department name cannot be empty")
            }
            
            // Check if another department with this name already exists
            val trimmedName = newName.trim()
            val existingDepartment = departmentRepository.findByNameIgnoreCase(trimmedName)
            if (existingDepartment != null && existingDepartment.id != departmentId) {
                throw DepartmentAlreadyExistsException(trimmedName)
            }
            
            department.name = trimmedName
        }
        
        try {
            val savedDepartment = departmentRepository.save(department)
            // Force initialization of batches collection
            savedDepartment.batches.size
            return savedDepartment
        } catch (e: Exception) {
            throw DepartmentServiceException("Failed to update department: ${e.message}")
        }
    }

    @Transactional(readOnly = true)
    fun getDepartmentById(departmentId: UUID): Department {
        val department = departmentRepository.findById(departmentId).orElseThrow {
            DepartmentNotFoundException(departmentId)
        }
        // Force initialization of batches collection
        department.batches.size
        return department
    }

    fun deleteDepartment(departmentId: UUID) {
        val department = departmentRepository.findById(departmentId).orElseThrow {
            DepartmentNotFoundException(departmentId)
        }
        
        // Check if department has any batches before deletion
        if (department.batches.isNotEmpty()) {
            throw DepartmentDeleteException("Cannot delete department '${department.name}' because it has ${department.batches.size} batch(es) assigned to it")
        }
        
        try {
            departmentRepository.delete(department)
        } catch (e: Exception) {
            throw DepartmentServiceException("Failed to delete department: ${e.message}")
        }
    }

    // Legacy method for backwards compatibility
    fun addDepartment(department: Department): Department{
        return departmentRepository.save(department)
    }    @Transactional(readOnly = true)
    fun findDepartmentById(departmentId: UUID): Department?{
        val department = departmentRepository.findByIdOrNull(departmentId)
        // Force initialization of batches collection if department exists
        department?.batches?.size
        return department
    }

    fun getBatches(department: Department): MutableSet<Batch>{
        return department.batches
    }

    fun getBatchesByDepartmentId(departmentId: UUID): List<Batch> {
        return departmentRepository.findBatchesByDepartmentId(departmentId)
    }
    fun getDepartmentCount(): Long {
        return departmentRepository.count()
    }
}

fun Department.toDepartmentResponse(): DepartmentResponse {
    val batchResponses = this.batches.map { batch ->
        DepartmentBatchResponse(
            id = batch.id.toString(),
            name = batch.name,
            graduationYear = batch.graduationYear,
            section = batch.section
        )
    }
    
    return DepartmentResponse(
        id = this.id.toString(),
        name = this.name,
        batches = batchResponses
    )
}

