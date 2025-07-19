package com.evalify.evalifybackend.quiz.controller

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.service.QuizTagsService
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * Controller managing quiz tags and categorization.
 *
 * Use Cases:
 * - Managing semester-specific quiz tags
 * - Creating and updating tag metadata
 * - Organizing quizzes through tagging
 * - Tag-based quiz categorization
 * - Managing tag hierarchies and relationships
 *
 * This controller handles the organization and management of quiz tags,
 * facilitating quiz categorization and organization within semesters.
 */
@RestController
@RequestMapping("/api/")
class QuizTagsController(
    private val quizTagsService: QuizTagsService,
    private val semesterRepository: SemesterRepository
) {
    /**
     * Retrieves all tags associated with a specific semester.
     *
     * Use Cases:
     * - Displaying available quiz categories
     * - Tag-based quiz filtering
     * - Managing semester organization
     * - Quiz discovery and navigation
     *
     * @param semesterId UUID of the semester to get tags for
     * @return ResponseEntity with list of QuizTagsReturnDTO
     */
    @GetMapping("semester/{semesterId}/tags")
    fun getTagsBySemesterId(@PathVariable semesterId: UUID
    ):ResponseEntity<List<QuizTagsReturnDTO>>{
        val result = quizTagsService.getTags(semesterId)
        return ResponseEntity.ok(
            result
        )

    }
    /**
     * Creates new tags for a specific semester.
     *
     * Use Cases:
     * - Organizing quizzes by topics
     * - Creating course-specific categorization
     * - Managing quiz metadata
     * - Facilitating quiz discovery
     *
     * @param semesterId UUID of the semester to create tags for
     * @param name Name of the new tag
     * @param description Optional description of the tag
     * @return ResponseEntity with creation status
     * @throws Exception if tag creation fails
     */
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