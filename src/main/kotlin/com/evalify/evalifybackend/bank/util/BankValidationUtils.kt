package com.evalify.evalifybackend.bank.util

import com.evalify.evalifybackend.bank.domain.DTO.bank.CreateBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.bank.EditBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.CreateQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.CreateTopicDTO
import com.evalify.evalifybackend.bank.exception.BankValidationException
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import java.util.UUID

/** Utility class for bank-related validations */
object BankValidationUtils {

    private const val MIN_BANK_NAME_LENGTH = 3
    private const val MAX_BANK_NAME_LENGTH = 100
    private const val MIN_COURSE_CODE_LENGTH = 2
    private const val MAX_COURSE_CODE_LENGTH = 10
    private const val MIN_SEMESTER = 1
    private const val MAX_SEMESTER = 8
    private const val MIN_TOPIC_NAME_LENGTH = 2
    private const val MAX_TOPIC_NAME_LENGTH = 50

    /** Validates bank creation/edit data */
    fun validateBankCreateData(dto: CreateBankDTO) {
        validateBankName(dto.name)
        dto.courseCode?.let { validateCourseCode(it) }
        validateSemester(dto.semester)
    }

    fun validateCreateData(dto: EditBankDTO) {
        validateBankName(dto.name)
        dto.courseCode?.let { validateCourseCode(it) }
        validateSemester(dto.semester)
    }

    /** Validates topic creation/edit data */
    fun validateTopicData(dto: CreateTopicDTO) {
        validateTopicName(dto.name)
    }

    /** Validates question creation/edit data */
    fun validateQuestionData(dto: CreateQuestionDTO) {
        validateQuestionTitle(dto.question)
        validateQuestionDescription(dto.explanation)

        // Add coding question test case validation
        if (dto.type == QuestionTypes.CODING) {
            validateCodingQuestionTestCases(dto.language, dto.testcases)
        }
    }

    fun validateEditQuestionData(dto: PatchQuestionDTO) {
        validateQuestionTitle(dto.question)
        validateQuestionDescription(dto.explanation)
        //validateTopicIds(dto.topic?.map { it.id } ?: emptyList())
    }

    private fun validateBankName(name: String?) {

        if(name == null) {
            throw BankValidationException("Name cannot be null or empty")
        }
        if (name.isBlank()) {
            throw BankValidationException("Bank name cannot be empty", "name")
        }
        if (name.length < MIN_BANK_NAME_LENGTH) {
            throw BankValidationException(
                    "Bank name must be at least $MIN_BANK_NAME_LENGTH characters long",
                    "name"
            )
        }
        if (name.length > MAX_BANK_NAME_LENGTH) {
            throw BankValidationException(
                    "Bank name cannot exceed $MAX_BANK_NAME_LENGTH characters",
                    "name"
            )
        }
    }

    private fun validateCourseCode(courseCode: String) {
        if (courseCode.isBlank()) {
            throw BankValidationException("Course code cannot be empty", "courseCode")
        }
        if (courseCode.length < MIN_COURSE_CODE_LENGTH) {
            throw BankValidationException(
                    "Course code must be at least $MIN_COURSE_CODE_LENGTH characters long",
                    "courseCode"
            )
        }
        if (courseCode.length > MAX_COURSE_CODE_LENGTH) {
            throw BankValidationException(
                    "Course code cannot exceed $MAX_COURSE_CODE_LENGTH characters",
                    "courseCode"
            )
        }
        if (!courseCode.matches(Regex("^[A-Z0-9]+$"))) {
            throw BankValidationException(
                    "Course code must contain only uppercase letters and numbers",
                    "courseCode"
            )
        }
    }

    private fun validateSemester(semester: Int?) {
        if(semester == null) {
            throw BankValidationException("Semester cannot be null or empty")
        }
        if (semester < MIN_SEMESTER || semester > MAX_SEMESTER) {
            throw BankValidationException(
                    "Semester must be between $MIN_SEMESTER and $MAX_SEMESTER",
                    "semester"
            )
        }
    }

    private fun validateTopicName(name: String) {
        if (name.isBlank()) {
            throw BankValidationException("Topic name cannot be empty", "name")
        }
        if (name.length < MIN_TOPIC_NAME_LENGTH) {
            throw BankValidationException(
                    "Topic name must be at least $MIN_TOPIC_NAME_LENGTH characters long",
                    "name"
            )
        }
        if (name.length > MAX_TOPIC_NAME_LENGTH) {
            throw BankValidationException(
                    "Topic name cannot exceed $MAX_TOPIC_NAME_LENGTH characters",
                    "name"
            )
        }
    }

    private fun validateQuestionTitle(title: String?) {

        if(title == null) {
            throw BankValidationException("Question title cannot be null or empty")
        }

        if (title.isBlank()) {
            throw BankValidationException("Question title cannot be empty", "title")
        }
        if (title.length > 2000) {
            throw BankValidationException("Question title cannot exceed 2000 characters", "title")
        }
    }

    private fun validateQuestionDescription(description: String?) {
        if (description != null && description.length > 2000) {
            throw BankValidationException(
                    "Question description cannot exceed 2000 characters",
                    "description"
            )
        }
    }

    private fun validateTopicIds(topicIds: List<UUID?>) {
        if (topicIds.isEmpty()) {
            throw BankValidationException("At least one topic must be selected", "topicIds")
        }
    }

    /**
     * Validates that for every language specified, there is at least one test case for that language.
     */
    private fun validateCodingQuestionTestCases(languages: List<String>?, testcases: List<com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO>?) {
        if (languages.isNullOrEmpty()) {
            throw BankValidationException("At least one language must be specified for coding questions", "language")
        }
        if (testcases.isNullOrEmpty()) {
            throw BankValidationException("At least one test case must be provided for coding questions", "testcases")
        }
        val missingLanguages = languages.filter { lang ->
            testcases.none { it.language == lang }
        }
        if (missingLanguages.isNotEmpty()) {
            throw BankValidationException(
                "Test cases must be provided for all languages: missing for ${missingLanguages.joinToString()}",
                "testcases"
            )
        }
    }
}
