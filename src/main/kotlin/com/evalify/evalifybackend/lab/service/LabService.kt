package com.evalify.evalifybackend.lab.service

import com.evalify.evalifybackend.common.logging.logger
import com.evalify.evalifybackend.lab.exception.LabNotFoundException
import com.evalify.evalifybackend.lab.exception.LabAlreadyExistsException
import com.evalify.evalifybackend.lab.exception.LabValidationException
import com.evalify.evalifybackend.lab.exception.LabServiceException
import com.evalify.evalifybackend.lab.exception.LabDeleteException
import com.evalify.evalifybackend.lab.exception.InvalidLabFieldException
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
            LabNotFoundException(labId)
        }
        return lab.toLabResponse()
    }

    fun createLab(createLabRequest: CreateLabRequest): LabResponse {
        // Validate input
        validateLabInput(createLabRequest.name, createLabRequest.block, createLabRequest.ipSubnet)
        
        // Check for duplicates
        checkForDuplicates(createLabRequest.name, createLabRequest.block, createLabRequest.ipSubnet)
        
        try {
            val lab = Lab(
                name = createLabRequest.name.trim(),
                block = createLabRequest.block.trim(),
                ipSubnet = createLabRequest.ipSubnet.trim()
            )
            val savedLab = labRepository.save(lab)
            return savedLab.toLabResponse()
        } catch (e: Exception) {
            logger.error("Error creating lab: {}", e.message, e)
            throw LabServiceException("Failed to create lab: ${e.message}")
        }
    }

    fun updateLab(labId: UUID, updateLabRequest: UpdateLabRequest): LabResponse {
        val existingLab = labRepository.findById(labId).orElseThrow {
            LabNotFoundException(labId)
        }

        // Validate and update fields if provided
        updateLabRequest.name?.let { newName ->
            if (newName.isBlank()) {
                throw LabValidationException("Lab name cannot be empty")
            }
            val trimmedName = newName.trim()
            
            // Check if another lab with this name exists
            val existingLabWithName = labRepository.findByNameIgnoreCase(trimmedName)
            if (existingLabWithName != null && existingLabWithName.id != labId) {
                throw LabAlreadyExistsException("Lab with name '$trimmedName' already exists")
            }
            
            existingLab.name = trimmedName
        }
        
        updateLabRequest.block?.let { newBlock ->
            if (newBlock.isBlank()) {
                throw LabValidationException("Lab block cannot be empty")
            }
            val trimmedBlock = newBlock.trim()
            
            // Check if another lab with this block exists
            val existingLabWithBlock = labRepository.findByBlockIgnoreCase(trimmedBlock)
            if (existingLabWithBlock != null && existingLabWithBlock.id != labId) {
                throw LabAlreadyExistsException("Lab with block '$trimmedBlock' already exists")
            }
            
            existingLab.block = trimmedBlock
        }
        
        updateLabRequest.ipSubnet?.let { newIpSubnet ->
            if (newIpSubnet.isBlank()) {
                throw LabValidationException("IP subnet cannot be empty")
            }
            val trimmedIpSubnet = newIpSubnet.trim()
            
            // Validate IP subnet format
            if (!isValidIpSubnet(trimmedIpSubnet)) {
                throw InvalidLabFieldException("ipSubnet", trimmedIpSubnet)
            }
            
            // Check if another lab with this IP subnet exists
            val existingLabWithIpSubnet = labRepository.findByIpSubnet(trimmedIpSubnet)
            if (existingLabWithIpSubnet != null && existingLabWithIpSubnet.id != labId) {
                throw LabAlreadyExistsException("Lab with IP subnet '$trimmedIpSubnet' already exists")
            }
            
            existingLab.ipSubnet = trimmedIpSubnet
        }

        try {
            val updatedLab = labRepository.save(existingLab)
            return updatedLab.toLabResponse()
        } catch (e: Exception) {
            logger.error("Error updating lab with id {}: {}", labId, e.message, e)
            throw LabServiceException("Failed to update lab: ${e.message}")
        }
    }

    fun deleteLab(labId: UUID): LabResponse {
        val lab = labRepository.findById(labId).orElseThrow {
            LabNotFoundException(labId)
        }
        
        // Check if lab has any associated lab assistants
        if (lab.labAssistant.isNotEmpty()) {
            throw LabDeleteException("Cannot delete lab '${lab.name}' because it has ${lab.labAssistant.size} lab assistant(s) assigned to it")
        }
        
        // Check if lab has any associated quizzes
        if (lab.quiz.isNotEmpty()) {
            throw LabDeleteException("Cannot delete lab '${lab.name}' because it has ${lab.quiz.size} quiz(zes) assigned to it")
        }
        
        try {
            labRepository.delete(lab)
            return lab.toLabResponse()
        } catch (e: Exception) {
            logger.error("Error deleting lab with id {}: {}", labId, e.message, e)
            throw LabServiceException("Failed to delete lab: ${e.message}")
        }
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
    
    // Validation helper methods
    private fun validateLabInput(name: String, block: String, ipSubnet: String) {
        if (name.isBlank()) {
            throw LabValidationException("Lab name cannot be empty")
        }
        if (block.isBlank()) {
            throw LabValidationException("Lab block cannot be empty")
        }
        if (ipSubnet.isBlank()) {
            throw LabValidationException("IP subnet cannot be empty")
        }
        if (!isValidIpSubnet(ipSubnet.trim())) {
            throw InvalidLabFieldException("ipSubnet", ipSubnet.trim())
        }
    }
    
    private fun checkForDuplicates(name: String, block: String, ipSubnet: String) {
        val trimmedName = name.trim()
        val trimmedBlock = block.trim()
        val trimmedIpSubnet = ipSubnet.trim()
        
        labRepository.findByNameIgnoreCase(trimmedName)?.let {
            throw LabAlreadyExistsException("Lab with name '$trimmedName' already exists")
        }
        
        labRepository.findByBlockIgnoreCase(trimmedBlock)?.let {
            throw LabAlreadyExistsException("Lab with block '$trimmedBlock' already exists")
        }
        
        labRepository.findByIpSubnet(trimmedIpSubnet)?.let {
            throw LabAlreadyExistsException("Lab with IP subnet '$trimmedIpSubnet' already exists")
        }
    }
    
    private fun isValidIpSubnet(ipSubnet: String): Boolean {
        // Basic IP subnet validation (CIDR notation)
        val regex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)/(?:[0-9]|[1-2][0-9]|3[0-2])$"
        return ipSubnet.matches(regex.toRegex())
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