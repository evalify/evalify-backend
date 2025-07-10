package com.evalify.evalifybackend.lab.controller

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
        val result = labService.searchLabsPaginated(query, page, size, sort_by, sort_order)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}")
    fun getLabById(@PathVariable id: UUID): ResponseEntity<LabResponse> {
        return ResponseEntity.ok(labService.getLabById(id))
    }

    @PostMapping
    fun createLab(@RequestBody createLabRequest: CreateLabRequest): ResponseEntity<LabResponse> {
        val lab = labService.createLab(createLabRequest)
        return ResponseEntity(lab, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateLab(
        @PathVariable id: UUID,
        @RequestBody updateLabRequest: UpdateLabRequest
    ): ResponseEntity<LabResponse> {
        val lab = labService.updateLab(id, updateLabRequest)
        return ResponseEntity.ok(lab)
    }

    @DeleteMapping("/{id}")
    fun deleteLab(@PathVariable id: UUID): ResponseEntity<LabResponse> {
        val lab = labService.deleteLab(id)
        return ResponseEntity.ok(lab)
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

    @GetMapping("/count")
    fun getLabCount(): ResponseEntity<Map<String,Long>> {
        return ResponseEntity.ok(mapOf("count" to labService.getLabCount()))
    }
}