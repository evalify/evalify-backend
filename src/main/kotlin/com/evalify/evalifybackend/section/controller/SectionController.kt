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
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/quiz/{quizId}/section")
class SectionController(
    private val sectionRepository: SectionRepository,
    private val sectionService: SectionService
) {

    @PostMapping("/")
    fun createSection(@PathVariable quizId: UUID, @RequestBody dto: CreateSectionDTO): ResponseEntity<Any> {
        sectionService.createNewSection(quizId, dto.name)
        return ResponseEntity.status(HttpStatus.CREATED).body("Section created successfully")
    }

    @PutMapping("/{sectionId}")
    fun editSection(@PathVariable sectionId: UUID, @RequestBody dto: CreateSectionDTO): ResponseEntity<Any> {
        sectionService.editSection(sectionId, dto.name)
        return ResponseEntity.ok("Section updated successfully")
    }

    @DeleteMapping("/{sectionId}")
    fun deleteSection(@PathVariable sectionId: UUID): ResponseEntity<Any> {
        sectionService.deleteSection(sectionId)
        return ResponseEntity.ok("Section deleted successfully")
    }

    @GetMapping("/")
    fun getQuizSections(@PathVariable quizId: UUID): ResponseEntity<Any> {
        val result = sectionService.getSection(quizId)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{sectionId}")
    fun viewSection(@PathVariable sectionId: UUID): ResponseEntity<Any> {
        val result = sectionService.getSectionQuestions(sectionId)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{sectionId}/move")
    fun moveSectionQuestions(@PathVariable sectionId: UUID, @RequestBody dto: MoveSectionQuestionDTO): ResponseEntity<Any> {
        sectionService.moveQuestions(sectionId, dto.questions, dto.toSectionId)
        return ResponseEntity.ok("Questions moved successfully")
    }
}