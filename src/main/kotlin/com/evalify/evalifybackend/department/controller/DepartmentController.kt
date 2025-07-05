package com.evalify.evalifybackend.department.controller


import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.department.domain.Department
import com.evalify.evalifybackend.department.domain.dto.CreateDepartmentRequest
import com.evalify.evalifybackend.department.domain.dto.DepartmentBatchResponse
import com.evalify.evalifybackend.department.domain.dto.DepartmentResponse
import com.evalify.evalifybackend.department.domain.dto.SimpleDepartmentResponse
import com.evalify.evalifybackend.department.domain.dto.UpdateDepartmentRequest
import com.evalify.evalifybackend.department.service.DepartmentService
import com.evalify.evalifybackend.department.service.toDepartmentResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/department")
class DepartmentController(
    private val departmentService: DepartmentService,
) {
    // Get all departments with pagination and sorting
    @GetMapping
    fun getAllDepartments(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            val departments = departmentService.getAllDepartments(page, size, sort_by, sort_order)
            ResponseEntity.ok(departments)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch departments: ${e.message}"))
        }
    }

    // Search departments with pagination
    @GetMapping("/search")
    fun searchDepartments(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) sort_by: String?,
        @RequestParam(required = false) sort_order: String?
    ): ResponseEntity<PaginatedResponse<DepartmentResponse>> {
        return try {
            val departments = departmentService.searchDepartments(query, page, size, sort_by, sort_order)
            ResponseEntity.ok(departments)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    PaginatedResponse(
                        data = emptyList(),
                        pagination = PaginationInfo(0, size, 0, 0)
                    )
                )
        }
    }

    @PostMapping
    fun createDepartment(@RequestBody request: CreateDepartmentRequest): DepartmentResponse {
        val newDepartment = departmentService.createDepartment(request)
        return newDepartment.toDepartmentResponse()
    }

    @PutMapping("/{departmentId}")
    fun updateDepartment(
        @PathVariable departmentId: UUID,
        @RequestBody request: UpdateDepartmentRequest
    ): DepartmentResponse {
        val updatedDepartment = departmentService.updateDepartment(departmentId, request)
        return updatedDepartment.toDepartmentResponse()
    }

    @DeleteMapping("/{departmentId}")
    fun deleteDepartment(@PathVariable departmentId: UUID): ResponseEntity<Any> {
        departmentService.deleteDepartment(departmentId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/all")
    fun getAllDepartmentsLegacy(): List<SimpleDepartmentResponse> {
        return departmentService.getAllDepartments().map { it.toSimpleDepartmentResponse() }
    }

    // Get a specific department by ID
    @GetMapping("/{departmentId}")
    fun getDepartmentById(@PathVariable departmentId: UUID): ResponseEntity<Any> {
        return try {
            val department = departmentService.getDepartmentById(departmentId)
            ResponseEntity.ok(department.toDepartmentResponse())
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to "Department not found: ${e.message}"))
        }
    }

    @GetMapping("/{departmentId}/batches")
    fun getBatches(@PathVariable("departmentId") departmentId: UUID): List<DepartmentBatchResponse> {
        return departmentService.getBatchesByDepartmentId(departmentId).map { batch ->
            batch.toDepartmentBatchResponse()
        }
    }

    @GetMapping("/count")
    fun getDepartmentCount(): ResponseEntity<Map<String, Long>> {
        return try {
            val count = departmentService.getDepartmentCount()
            ResponseEntity.ok(mapOf("count" to count))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

    fun Department.toSimpleDepartmentResponse(): SimpleDepartmentResponse {
        return SimpleDepartmentResponse(
            id = this.id.toString(),
            name = this.name
        )
    }
}

fun Batch.toDepartmentBatchResponse(): DepartmentBatchResponse {
    return DepartmentBatchResponse(
        id = this.id.toString(),
        name = this.name,
        graduationYear = this.graduationYear,
        section = this.section,
    )
}


