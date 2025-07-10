package com.evalify.evalifybackend.quiz.util

import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.CreateQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.PatchQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.CreateQuizQuestionDTO
import com.evalify.evalifybackend.quiz.exception.QuizValidationException
import com.evalify.evalifybackend.quiz.exception.SectionValidationException
import java.time.Instant
import java.util.UUID

/** Utility class for quiz-related validations */
object QuizValidationUtils {

    private const val MIN_QUIZ_NAME_LENGTH = 3
    private const val MAX_QUIZ_NAME_LENGTH = 100
    private const val MIN_QUIZ_DURATION_MINUTES = 1L
    private const val MAX_QUIZ_DURATION_MINUTES = 720L // 12 hours
    private const val MAX_DESCRIPTION_LENGTH = 2000
    private const val MAX_INSTRUCTIONS_LENGTH = 5000

    /** Validates quiz creation data */
    fun validateCreateQuizData(dto: CreateQuizDTO) {
        validateQuizName(dto.name)
        validateQuizTiming(dto.startTime, dto.endTime, dto.durationInMinutes)
        dto.description?.let { validateDescription(it) }
        dto.instructions?.let { validateInstructions(it) }
//        validateQuizCollections(dto.courseIds, dto.batchIds, dto.labIds, dto.studentIds)
    }

    /** Validates quiz patch data */
    fun validatePatchQuizData(dto: PatchQuizDTO) {
        dto.name?.let { validateQuizName(it) }
        if (dto.startTime != null && dto.endTime != null) {
            validateQuizTiming(dto.startTime, dto.endTime, dto.durationInMinutes ?: 30L)
        }
        dto.description?.let { validateDescription(it) }
        dto.instructions?.let { validateInstructions(it) }
    }

    /** Validates quiz question creation data */
    fun validateCreateQuizQuestionData(dto: CreateQuizQuestionDTO) {
        validateQuestionTitle(dto.question)
        validateQuestionMarks(dto.marks)
        validateNegativeMarks(dto.negativeMark, dto.marks)
        dto.explanation?.let { validateExplanation(it) }
        dto.hint?.let { validateHint(it) }
        validateTopicIds(dto.topicIds)
        validateSectionId(dto.sectionId)
    }

    /** Validates section operations */
    fun validateSectionOperation(sectionId: UUID) {
        if (sectionId.toString().isBlank()) {
            throw SectionValidationException("Section ID cannot be empty", "sectionId")
        }
    }

    private fun validateQuizName(name: String) {
        if (name.isBlank()) {
            throw QuizValidationException("Quiz name cannot be empty", "name")
        }
        if (name.length < MIN_QUIZ_NAME_LENGTH) {
            throw QuizValidationException(
                    "Quiz name must be at least $MIN_QUIZ_NAME_LENGTH characters long",
                    "name"
            )
        }
        if (name.length > MAX_QUIZ_NAME_LENGTH) {
            throw QuizValidationException(
                    "Quiz name cannot exceed $MAX_QUIZ_NAME_LENGTH characters",
                    "name"
            )
        }
    }

    private fun validateQuizTiming(startTime: Instant, endTime: Instant, durationInMinutes: Long) {
        val now = Instant.now()

        if (startTime.isBefore(now.minusSeconds(60))) { // Allow 1 minute tolerance
            throw QuizValidationException("Quiz start time cannot be in the past", "startTime")
        }

        if (endTime.isBefore(startTime)) {
            throw QuizValidationException("Quiz end time must be after start time", "endTime")
        }

        if (durationInMinutes < MIN_QUIZ_DURATION_MINUTES) {
            throw QuizValidationException(
                    "Quiz duration must be at least $MIN_QUIZ_DURATION_MINUTES minute(s)",
                    "durationInMinutes"
            )
        }

        if (durationInMinutes > MAX_QUIZ_DURATION_MINUTES) {
            throw QuizValidationException(
                    "Quiz duration cannot exceed $MAX_QUIZ_DURATION_MINUTES minutes",
                    "durationInMinutes"
            )
        }

        // Duration should not exceed the time window
        val timeWindowMinutes = java.time.Duration.between(startTime, endTime).toMinutes()
        if (durationInMinutes > timeWindowMinutes) {
            throw QuizValidationException(
                    "Quiz duration ($durationInMinutes minutes) cannot exceed the time window ($timeWindowMinutes minutes)",
                    "durationInMinutes"
            )
        }
    }

    private fun validateDescription(description: String) {
        if (description.length > MAX_DESCRIPTION_LENGTH) {
            throw QuizValidationException(
                    "Quiz description cannot exceed $MAX_DESCRIPTION_LENGTH characters",
                    "description"
            )
        }
    }

    private fun validateInstructions(instructions: String) {
        if (instructions.length > MAX_INSTRUCTIONS_LENGTH) {
            throw QuizValidationException(
                    "Quiz instructions cannot exceed $MAX_INSTRUCTIONS_LENGTH characters",
                    "instructions"
            )
        }
    }

    private fun validateQuizCollections(
            courseIds: List<UUID>,
            batchIds: List<UUID>,
            @Suppress("UNUSED_PARAMETER") labIds: List<UUID>,
            studentIds: MutableList<String>
    ) {
        if (courseIds.isEmpty()) {
            throw QuizValidationException("At least one course must be selected", "courseIds")
        }

        if (batchIds.isEmpty() && studentIds.isEmpty()) {
            throw QuizValidationException(
                    "At least one batch or student must be selected",
                    "batchIds/studentIds"
            )
        }
    }

    private fun validateQuestionTitle(title: String) {
        if (title.isBlank()) {
            throw QuizValidationException("Question title cannot be empty", "question")
        }
        if (title.length > 2000) {
            throw QuizValidationException(
                    "Question title cannot exceed 2000 characters",
                    "question"
            )
        }
    }

    private fun validateQuestionMarks(marks: Int) {
        if (marks <= 0) {
            throw QuizValidationException("Question marks must be positive", "marks")
        }
        if (marks > 100) {
            throw QuizValidationException("Question marks cannot exceed 100", "marks")
        }
    }

    private fun validateNegativeMarks(negativeMark: Int?, totalMarks: Int) {
        if (negativeMark != null) {
            if (negativeMark < 0) {
                throw QuizValidationException(
                        "Negative marks cannot be negative value",
                        "negativeMark"
                )
            }
            if (negativeMark > totalMarks) {
                throw QuizValidationException(
                        "Negative marks cannot exceed total marks",
                        "negativeMark"
                )
            }
        }
    }

    private fun validateExplanation(explanation: String) {
        if (explanation.length > 2000) {
            throw QuizValidationException(
                    "Question explanation cannot exceed 2000 characters",
                    "explanation"
            )
        }
    }

    private fun validateHint(hint: String) {
        if (hint.length > 500) {
            throw QuizValidationException("Question hint cannot exceed 500 characters", "hint")
        }
    }

    private fun validateTopicIds(topicIds: List<UUID>) {
        if (topicIds.isEmpty()) {
            throw QuizValidationException("At least one topic must be selected", "topicIds")
        }
        if (topicIds.size > 10) {
            throw QuizValidationException("Cannot select more than 10 topics", "topicIds")
        }
    }

    private fun validateSectionId(sectionId: UUID) {
        if (sectionId.toString().isBlank()) {
            throw SectionValidationException("Section ID cannot be empty", "sectionId")
        }
    }
}
