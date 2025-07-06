package com.evalify.evalifybackend.lab.service

import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.core.pagination.PaginatedResponse
import com.evalify.evalifybackend.core.pagination.PaginationInfo
import com.evalify.evalifybackend.lab.domain.DTO.CreateLabRequest
import com.evalify.evalifybackend.lab.domain.DTO.LabResponse
import com.evalify.evalifybackend.lab.domain.DTO.UpdateLabRequest
import com.evalify.evalifybackend.lab.domain.Lab
import com.evalify.evalifybackend.lab.repository.LabRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class LabService(
    private val labRepository: LabRepository
) {
    
    private val logger by logger()

    fun getAllLabsPaginated(page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<LabResponse> {
        val sort = createSort(sortBy, sortOrder)
        val pageable = PageRequest.of(page, size, sort)
        val labPage: Page<Lab> = labRepository.findAll(pageable)

        return PaginatedResponse(
            data = labPage.content.map { it.toLabResponse() },
            pagination = PaginationInfo(
                current_page = page,
                per_page = size,
                total_pages = labPage.totalPages,
                total_count = labPage.totalElements.toInt()
            )
        )
    }

    fun searchLabsPaginated(query: String, page: Int, size: Int, sortBy: String = "name", sortOrder: String = "asc"): PaginatedResponse<LabResponse> {
        logger.info("Searching labs with query: '{}', page: {}, size: {}, sortBy: {}, sortOrder: {}", query, page, size, sortBy, sortOrder)
        
        try {
            val sort = createSort(sortBy, sortOrder)
            val pageable = PageRequest.of(page, size, sort)
            val labPage: Page<Lab> = labRepository.findByNameOrBlockOrIpSubnetContainingIgnoreCasePage(query, pageable)

            logger.debug("Found {} labs from {} total", labPage.content.size, labPage.totalElements)

            return PaginatedResponse(
                data = labPage.content.map { it.toLabResponse() },
                pagination = PaginationInfo(
                    current_page = page,
                    per_page = size,
                    total_pages = labPage.totalPages,
                    total_count = labPage.totalElements.toInt()
                )
            )
        } catch (e: Exception) {
            logger.error("Error searching labs with query: '{}', error: {}", query, e.message, e)
            throw e
        }
    }

    private fun createSort(sortBy: String, sortOrder: String): Sort {
        val direction = if (sortOrder.lowercase() == "desc") Sort.Direction.DESC else Sort.Direction.ASC
        
        // Validate sortBy field to prevent SQL injection and invalid field errors
        val validSortFields = setOf("id", "name", "block", "ipSubnet")
        val validatedSortBy = if (validSortFields.contains(sortBy)) {
            sortBy
        } else {
            logger.warn("Invalid sort field '{}', defaulting to 'name'", sortBy)
            "name" // Default to name if invalid field provided
        }
        
        logger.debug("Creating sort with field: {}, direction: {}", validatedSortBy, direction)
        return Sort.by(direction, validatedSortBy)
    }

    fun getLabById(labId: UUID): LabResponse {
        val lab = labRepository.findById(labId).orElseThrow {
            NotFoundException("Lab with id $labId not found")
        }
        return lab.toLabResponse()
    }

    fun createLab(createLabRequest: CreateLabRequest): LabResponse {
        val lab = Lab(
            name = createLabRequest.name,
            block = createLabRequest.block,
            ipSubnet = createLabRequest.ipSubnet
        )
        val savedLab = labRepository.save(lab)
        return savedLab.toLabResponse()
    }

    fun updateLab(labId: UUID, updateLabRequest: UpdateLabRequest): LabResponse {
        val existingLab = labRepository.findById(labId).orElseThrow {
            NotFoundException("Lab with id $labId not found")
        }

        updateLabRequest.name?.let { existingLab.name = it }
        updateLabRequest.block?.let { existingLab.block = it }
        updateLabRequest.ipSubnet?.let { existingLab.ipSubnet = it }

        val updatedLab = labRepository.save(existingLab)
        return updatedLab.toLabResponse()
    }

    fun deleteLab(labId: UUID): LabResponse {
        val lab = labRepository.findById(labId).orElseThrow {
            NotFoundException("Lab with id $labId not found")
        }
        labRepository.delete(lab)
        return lab.toLabResponse()
    }

    // Legacy methods for backwards compatibility
    fun getAllLabs(): List<LabResponse> {
        return labRepository.findAll().map { it.toLabResponse() }
    }

    fun searchLabs(query: String): List<LabResponse> {
        return labRepository.findByNameOrBlockOrIpSubnetContainingIgnoreCase(query).map { it.toLabResponse() }
    }

    fun getLabCount(): Long {
        return labRepository.count()
    }
}

fun Lab.toLabResponse(): LabResponse {
    return LabResponse(
        id = this.id,
        name = this.name,
        block = this.block,
        ipSubnet = this.ipSubnet
    )
}