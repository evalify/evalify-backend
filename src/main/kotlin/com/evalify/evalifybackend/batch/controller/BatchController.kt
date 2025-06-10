package com.evalify.evalifybackend.batch.controller


import com.evalify.evalifybackend.batch.domain.DTO.BatchResponse
import com.evalify.evalifybackend.batch.domain.DTO.SemesterBatchResponse
import com.evalify.evalifybackend.batch.domain.DTO.UpdateBatchBankDTO
import com.evalify.evalifybackend.batch.domain.DTO.CreateBatchRequest
import com.evalify.evalifybackend.batch.domain.DTO.UpdateBatchRequest
import com.evalify.evalifybackend.batch.service.BatchBankService
import com.evalify.evalifybackend.batch.service.BatchService
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.semester.domain.Semester
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/batch")
class BatchController(val batchBankService: BatchBankService, val batchService: BatchService) {
    @PutMapping("{batchId}/add-bank")
    fun addBankToBatch(@RequestBody bankDto:UpdateBatchBankDTO,@PathVariable batchId:UUID){
        batchBankService.addBank(batchId = batchId, bankId = bankDto.bank)
    }
    @PutMapping("{batchId}/remove-bank")
    fun removeBankFromCourse(@RequestBody bankDto:UpdateBatchBankDTO,@PathVariable batchId:UUID){
        batchBankService.removeBank(batchId = batchId, bankId = bankDto.bank)
    }

    @PutMapping("/{batchId}/add-students")
    fun addStudents(@RequestBody userIds: List<UUID>, @PathVariable batchId: UUID) {
        batchService.addStudentsToBatch(batchId, userIds)
    }

    @PutMapping("/{batchId}/delete-students")
    fun deleteStudents(@RequestBody userIds: List<UUID>, @PathVariable batchId: UUID) {
        batchService.removeStudentsFromBatch(batchId, userIds)
    }

    @PutMapping("/{batchId}/add-semesters")
    fun addSemesters(@RequestBody semesterIds: List<UUID>, @PathVariable batchId: UUID) {
        batchService.addSemestersToBatches(batchId, semesterIds)
    }

    @PutMapping("/{batchId}/delete-semesters")
    fun deleteSemesters(@RequestBody semesterIds: List<UUID>, @PathVariable batchId: UUID) {
        batchService.removeSemestersFromBatch(batchId, semesterIds)
    }

    @PutMapping("/{batchId}/assign-managers")
    fun assignManagers(@RequestBody userIds: List<UUID>, @PathVariable batchId: UUID) {
        batchService.addManagersToBatch(batchId, userIds)
    }

    @PutMapping("/{batchId}/remove-managers")
    fun removeManagers(@RequestBody userIds: List<UUID>, @PathVariable batchId: UUID) {
        batchService.removeManagersFromBatch(batchId, userIds)
    }

    @PostMapping
    fun createBatch(@RequestBody createBatchRequest: CreateBatchRequest): ResponseEntity<BatchResponse> {
        val batch = batchService.createBatch(createBatchRequest)
        return ResponseEntity.ok(batch)
    }

    @PutMapping("/{batchId}")
    fun updateBatch(
        @PathVariable batchId: UUID,
        @RequestBody updateBatchRequest: UpdateBatchRequest
    ): ResponseEntity<BatchResponse> {
        val batch = batchService.updateBatch(batchId, updateBatchRequest)
        return ResponseEntity.ok(batch)
    }

    @GetMapping
    fun getAllBatches(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String?
    ): ResponseEntity<Any> {
        return try {
            val batches = batchService.getAllBatches(page, size, sort_by, sort_order)
            ResponseEntity.ok(batches)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch batches: ${e.message}"))
        }
    }

    @GetMapping("/search")
    fun searchBatches(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) sort_by: String?,
        @RequestParam(required = false) sort_order: String?
    ): ResponseEntity<PaginatedResponse<BatchResponse>> {
        return try {
            val batches = batchService.searchBatches(query, page, size, sort_by, sort_order)
            ResponseEntity.ok(batches)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PaginatedResponse(
                    data = emptyList(),
                    pagination = PaginationInfo(0, size, 0, 0)
                ))
        }
    }

    // Legacy search endpoint for backwards compatibility
    @GetMapping("/search/legacy")
    fun searchBatchesLegacy(@RequestParam query: String): ResponseEntity<List<BatchResponse>> {
        return ResponseEntity.ok(
            batchService.searchBatches(query)
        )
    }

    @GetMapping("/{batchId}/active-semester")
    fun getActiveSemester(@PathVariable batchId: UUID): ResponseEntity<SemesterBatchResponse> {
        val semester = batchService.getActiveSemester(batchId)
        if( semester == null) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(semester.toSemesterBatchResponse())
    }

    @GetMapping("/{batchId}")
    fun getBatchById(@PathVariable batchId: UUID): ResponseEntity<Any> {
        return try {
            val batch = batchService.getBatchById(batchId)
            ResponseEntity.ok(batch)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch batch: ${e.message}"))
        }
    }    @GetMapping("/{batchId}/students")
    fun getBatchStudents(
        @PathVariable batchId: UUID,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            val students = batchService.getBatchStudents(batchId, page, size, sort_by, sort_order)
            ResponseEntity.ok(students)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to fetch batch students: ${e.message}"))
        }
    }

    @GetMapping("/{batchId}/students/search")
    fun searchBatchStudents(
        @PathVariable batchId: UUID,
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<Any> {
        return try {
            val students = batchService.searchBatchStudents(batchId, query, page, size, sort_by, sort_order)
            ResponseEntity.ok(students)
        } catch (e: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Failed to search batch students: ${e.message}"))
        }
    }
}

fun Semester.toSemesterBatchResponse(): SemesterBatchResponse {
    return SemesterBatchResponse(
        id = this.id.toString(),
        name = this.name,
        year = this.year.toString(),
        isActive = this.isActive
    )
}