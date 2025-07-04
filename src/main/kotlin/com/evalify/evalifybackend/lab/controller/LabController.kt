package com.evalify.evalifybackend.lab.controller

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.lab.domain.DTO.CreateLabRequest
import com.evalify.evalifybackend.lab.domain.DTO.LabResponse
import com.evalify.evalifybackend.lab.domain.DTO.UpdateLabRequest
import com.evalify.evalifybackend.lab.service.LabService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/lab")
class LabController(
    private val labService: LabService
) {

    @GetMapping
    fun getAllLabs(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<LabResponse>> {
        return ResponseEntity.ok(
            labService.getAllLabsPaginated(page, size, sort_by, sort_order)
        )
    }

    @GetMapping("/search")
    fun searchLabs(
        @RequestParam query: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "name") sort_by: String,
        @RequestParam(defaultValue = "asc") sort_order: String
    ): ResponseEntity<PaginatedResponse<LabResponse>> {
        return ResponseEntity.ok(
            labService.searchLabsPaginated(query, page, size, sort_by, sort_order)
        )
    }

    @GetMapping("/{id}")
    fun getLabById(@PathVariable id: UUID): ResponseEntity<LabResponse> {
        return try {
            ResponseEntity.ok(labService.getLabById(id))
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createLab(@RequestBody createLabRequest: CreateLabRequest): ResponseEntity<LabResponse> {
        return try {
            val lab = labService.createLab(createLabRequest)
            ResponseEntity(lab, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @PutMapping("/{id}")
    fun updateLab(
        @PathVariable id: UUID,
        @RequestBody updateLabRequest: UpdateLabRequest
    ): ResponseEntity<LabResponse> {
        return try {
            val lab = labService.updateLab(id, updateLabRequest)
            ResponseEntity.ok(lab)
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteLab(@PathVariable id: UUID): ResponseEntity<LabResponse> {
        return try {
            val lab = labService.deleteLab(id)
            ResponseEntity.ok(lab)
        } catch (e: NotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }

    // Legacy endpoints for backwards compatibility
    @GetMapping("/all")
    fun getAllLabsLegacy(): ResponseEntity<List<LabResponse>> {
        return ResponseEntity.ok(labService.getAllLabs())
    }

    @GetMapping("/search-legacy")
    fun searchLabsLegacy(@RequestParam query: String): ResponseEntity<List<LabResponse>> {
        return ResponseEntity.ok(labService.searchLabs(query))
    }
}