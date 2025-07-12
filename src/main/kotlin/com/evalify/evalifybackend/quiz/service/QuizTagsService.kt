package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.quiz.domain.QuizTags
import com.evalify.evalifybackend.quiz.repository.QuizTagsRepository
import com.evalify.evalifybackend.semester.repository.SemesterRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuizTagsService(
    private val quizTagsRepository: QuizTagsRepository,
    private val semesterRepository: SemesterRepository
) {
    fun createTags(semesterId: UUID, name: String?, description: String?) {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            IllegalArgumentException("Semester not found for id: $semesterId")
        }
        val tags = QuizTags(
            name = name,
            description = description
        )
        semester.quizTags.add(tags)
        quizTagsRepository.save(tags)
        semesterRepository.save(semester)
    }
// Editing tags is independent of the semester
    fun editTags(tagId: UUID, name: String?, description: String?) {
        val tag = quizTagsRepository.findById(tagId).orElseThrow {
            IllegalArgumentException("QuizTag not found for id: $tagId")
        }
        val tags = QuizTags(
            id = tagId,
            name = name,
            description = description
        )

        quizTagsRepository.save(tags)
    }

    fun deleteTagsFromSemester(semesterId: UUID, tagId: UUID) {
        val semester = semesterRepository.findById(semesterId).orElseThrow {
            IllegalArgumentException("Semester not found for id: $semesterId")
        }
        val tag = quizTagsRepository.findById(tagId).orElseThrow {
            IllegalArgumentException("QuizTag not found for id: $tagId")
        }
        semester.quizTags.removeIf { it.id == tagId }
        semesterRepository.save(semester)
        quizTagsRepository.delete(tag)
    }

    fun deleteTags(tagId: UUID) = quizTagsRepository.deleteById(
        tagId
    )
}