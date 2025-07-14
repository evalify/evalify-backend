package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.service.QuizTagsService
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/")
class QuizTagsController(
    private val quizTagsService: QuizTagsService,
    private val semesterRepository: SemesterRepository
) {
    @GetMapping("semester/{semesterId}/tags")
    fun getTagsBySemesterId(@PathVariable semesterId: UUID
    ):ResponseEntity<List<QuizTagsReturnDTO>>{
        val result = quizTagsService.getTags(semesterId)
        return ResponseEntity.ok(
            result
        )

    }
    @PostMapping("semester/{semesterId}/tags")
    fun createTags(
        @PathVariable semesterId: UUID,
        name: String?,
        description: String? = null
    ): ResponseEntity<Any> {
        return try {
            quizTagsService.createTags(semesterId, name, description)
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Tag created successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }

    @PatchMapping("tags/{tagId}/edit")
    fun editTags(
        @PathVariable tagId: UUID,
        name: String?,
        description: String? = null
    ): ResponseEntity<Any> {
        return try {
            quizTagsService.editTags(tagId, name, description)
            ResponseEntity.ok(mapOf("message" to "Tag updated successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("tags/{tagId}/delete")
    fun deleteTags(
        @PathVariable tagId: UUID
    ): ResponseEntity<Any> {
        return try {
            quizTagsService.deleteTags(tagId)
            ResponseEntity.ok(mapOf("message" to "Tag deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }

    @DeleteMapping("semester/{semesterId}/tags/{tagId}/delete")
    fun deleteFromSemester(
        @PathVariable semesterId: UUID,
        @PathVariable tagId: UUID
    ): ResponseEntity<Any> {
        return try {
            quizTagsService.deleteTagsFromSemester(semesterId, tagId)
            ResponseEntity.ok(mapOf("message" to "Tag removed from semester successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to e.message))
        }
    }
}