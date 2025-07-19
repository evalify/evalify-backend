package com.evalify.evalifybackend.section.controller

import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.section.domain.DTO.CreateSectionDTO
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.section.domain.DTO.MoveSectionQuestionDTO
import com.evalify.evalifybackend.section.repository.SectionRepository
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import com.evalify.evalifybackend.section.service.SectionService
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import com.evalify.evalifybackend.core.exception.NotFoundException
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Controller managing quiz sections and their organization.
 *
 * Use Cases:
 * - Creating and managing quiz sections
 * - Organizing questions within sections
 * - Moving questions between sections
 * - Section-level configurations and settings
 * - Managing section structure and hierarchy
 *
 * This controller handles the organization and management of quiz sections,
 * allowing for structured question grouping and organization within quizzes.
 */
@RestController()
@RequestMapping("/api/quiz/{quizId}/section")
class SectionController(
    private val sectionRepository: SectionRepository,
    private val sectionService: SectionService
) {

    /**
     * Creates a new section within a quiz.
     *
     * Use Cases:
     * - Organizing quiz questions into logical groups
     * - Creating topic-based sections
     * - Structuring quiz content
     * - Managing question difficulty progression
     *
     * @param quizId UUID of the quiz to add the section to
     * @param dto CreateSectionDTO containing section details
     * @return ResponseEntity with creation confirmation
     * @throws NotFoundException if quiz doesn't exist
     */
    @PostMapping
    fun createSection(@PathVariable quizId: UUID, @RequestBody dto: CreateSectionDTO): ResponseEntity<Any> {
        return try {
            sectionService.createNewSection(quizId, dto.name)
            ResponseEntity.status(HttpStatus.CREATED).body("Section created successfully")
        } catch (ex: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }
    }

    @PutMapping("/{sectionId}")
    fun editSection(@PathVariable sectionId: UUID, @RequestBody dto: CreateSectionDTO): ResponseEntity<Any> {
        return try {
            sectionService.editSection(sectionId, dto.name)
            ResponseEntity.ok("Section updated successfully")
        } catch (ex: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }
    }

    @DeleteMapping("/{sectionId}")
    fun deleteSection(@PathVariable sectionId: UUID): ResponseEntity<Any> {
        return try {
            sectionService.deleteSection(sectionId)
            ResponseEntity.ok("Section deleted successfully")
        } catch (ex: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }
    }

    @GetMapping("")
    fun getQuizSections(@PathVariable quizId: UUID): ResponseEntity<List<GetSectionDTO>> {
        return try {

            val result = sectionService.getSection(quizId)
            ResponseEntity.ok(result)
        } catch (ex: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        } as ResponseEntity<List<GetSectionDTO>>
    }

    @GetMapping("/{sectionId}")
    fun viewSection(@PathVariable sectionId: UUID): ResponseEntity<Any> {
        return try {
            val result = sectionService.getSectionQuestions(sectionId)
            ResponseEntity.ok(result)
        } catch (ex: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }
    }

    /**
     * Moves questions between sections within a quiz.
     *
     * Use Cases:
     * - Reorganizing quiz structure
     * - Adjusting question order
     * - Managing section content
     * - Optimizing quiz flow
     *
     * @param sectionId UUID of the section containing questions to move
     * @param dto MoveSectionQuestionDTO containing movement details
     * @return ResponseEntity with movement confirmation
     * @throws NotFoundException if section or questions don't exist
     */
    @PostMapping("/{sectionId}/move")
    fun moveSectionQuestions(@PathVariable sectionId: UUID, @RequestBody dto: MoveSectionQuestionDTO): ResponseEntity<Any> {
        return try {
            sectionService.moveQuestions(sectionId, dto.questions, dto.toSectionId)
            ResponseEntity.ok("Questions moved successfully")
        } catch (ex: NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")
        }
    }
}