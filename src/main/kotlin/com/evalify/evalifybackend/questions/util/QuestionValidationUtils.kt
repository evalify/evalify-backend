package com.evalify.evalifybackend.questions.util

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.questions.exception.QuestionValidationException
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import java.util.UUID

/** Utility class for question-related validations */
object QuestionValidationUtils {

    private const val MIN_QUESTION_LENGTH = 10
    private const val MAX_QUESTION_LENGTH = 2000
    private const val MIN_MARKS = 1
    private const val MAX_MARKS = 100
    private const val MAX_EXPLANATION_LENGTH = 3000
    private const val MAX_HINT_LENGTH = 500
    private const val MIN_MCQ_OPTIONS = 2
    private const val MAX_MCQ_OPTIONS = 8
    private const val MIN_CODING_FUNCTION_NAME_LENGTH = 2
    private const val MAX_CODING_FUNCTION_NAME_LENGTH = 50

    /** Validates basic question data */
    fun validateBasicQuestionData(
            question: String,
            marks: Int,
            bloomsTaxonomy: Taxonomy,
            co: Int,
            negativeMark: Int?,
            difficulty: Difficulty,
            topicIds: List<UUID>
    ) {
        validateQuestionText(question)
        validateMarks(marks)
        validateNegativeMarks(negativeMark, marks)
        validateCO(co)
        validateTopicIds(topicIds)
        validateBloomsTaxonomy(bloomsTaxonomy)
        validateDifficulty(difficulty)
    }

    /** Validates MCQ specific data */
    fun validateMCQData(options: List<MCQOption>?) {
        if (options.isNullOrEmpty()) {
            throw QuestionValidationException("MCQ must have options", "options")
        }

        if (options.size < MIN_MCQ_OPTIONS) {
            throw QuestionValidationException(
                    "MCQ must have at least $MIN_MCQ_OPTIONS options",
                    "options"
            )
        }

        if (options.size > MAX_MCQ_OPTIONS) {
            throw QuestionValidationException(
                    "MCQ cannot have more than $MAX_MCQ_OPTIONS options",
                    "options"
            )
        }

        val correctOptions = options.count { it.isCorrect }
        if (correctOptions == 0) {
            throw QuestionValidationException(
                    "MCQ must have at least one correct option",
                    "options"
            )
        }

        // Validate option texts
        options.forEachIndexed { index, option ->
            if (option.text.isBlank()) {
                throw QuestionValidationException(
                        "Option ${index + 1} text cannot be empty",
                        "options"
                )
            }
            if (option.text.length > 500) {
                throw QuestionValidationException(
                        "Option ${index + 1} text cannot exceed 500 characters",
                        "options"
                )
            }
        }

        // Check for duplicate options
        val uniqueTexts = options.map { it.text.trim().lowercase() }.toSet()
        if (uniqueTexts.size != options.size) {
            throw QuestionValidationException("MCQ options must be unique", "options")
        }
    }

    /** Validates MMCQ specific data */
    fun validateMMCQData(options: List<MCQOption>?) {
        validateMCQData(options) // Same basic validation as MCQ

        if (options != null) {
            val correctOptions = options.count { it.isCorrect }
            if (correctOptions < 2) {
                throw QuestionValidationException(
                        "MMCQ must have at least 2 correct options",
                        "options"
                )
            }
        }
    }

    /** Validates coding question specific data */
    fun validateCodingQuestionData(
            functionName: String?,
            returnType: String?,
            driverCode: String?,
            boilerCode: String?
    ) {
        functionName?.let { validateFunctionName(it) }
        returnType?.let { validateReturnType(it) }
        driverCode?.let { validateDriverCode(it) }
        boilerCode?.let { validateBoilerCode(it) }
    }

    /** Validates true/false question data */
    fun validateTrueFalseData(answer: Boolean?) {
        if (answer == null) {
            throw QuestionValidationException("True/False question must have an answer", "answer")
        }
    }

    /** Validates fill-up question data */
    fun validateFillUpData(template: String?, blanks: List<Any>?) {
        if (template.isNullOrBlank()) {
            throw QuestionValidationException("Fill-up question must have a template", "template")
        }

        if (blanks.isNullOrEmpty()) {
            throw QuestionValidationException("Fill-up question must have blanks", "blanks")
        }

        if (blanks.size > 10) {
            throw QuestionValidationException(
                    "Fill-up question cannot have more than 10 blanks",
                    "blanks"
            )
        }
    }

    /** Validates descriptive question data */
    fun validateDescriptiveData(expectedAnswer: String?, guidelines: String?) {
        expectedAnswer?.let {
            if (it.length > 5000) {
                throw QuestionValidationException(
                        "Expected answer cannot exceed 5000 characters",
                        "expectedAnswer"
                )
            }
        }

        guidelines?.let {
            if (it.length > 2000) {
                throw QuestionValidationException(
                        "Guidelines cannot exceed 2000 characters",
                        "guidelines"
                )
            }
        }
    }

    /** Validates file upload question data */
    fun validateFileUploadData(expectedAnswer: String?, guidelines: String?) {
        // Same validation as descriptive for now
        validateDescriptiveData(expectedAnswer, guidelines)
    }

    /** Validates match the following question data */
    fun validateMatchTheFollowingData(keys: List<Any>?) {
        if (keys.isNullOrEmpty()) {
            throw QuestionValidationException(
                    "Match the following question must have key pairs",
                    "keys"
            )
        }

        if (keys.size < 3) {
            throw QuestionValidationException(
                    "Match the following question must have at least 3 pairs",
                    "keys"
            )
        }

        if (keys.size > 10) {
            throw QuestionValidationException(
                    "Match the following question cannot have more than 10 pairs",
                    "keys"
            )
        }
    }

    /** Validates question type consistency */
    fun validateQuestionTypeConsistency(
            questionType: QuestionTypes,
            hasOptions: Boolean,
            hasCodingData: Boolean
    ) {
        when (questionType) {
            QuestionTypes.MCQ, QuestionTypes.MMCQ -> {
                if (!hasOptions) {
                    throw QuestionValidationException(
                            "MCQ/MMCQ questions must have options",
                            "options"
                    )
                }
            }
            QuestionTypes.CODING -> {
                if (!hasCodingData) {
                    throw QuestionValidationException(
                            "Coding questions must have coding-specific data",
                            "codingData"
                    )
                }
            }
            else -> {
                // Other types don't require specific validation here
            }
        }
    }

    private fun validateQuestionText(question: String) {
        if (question.isBlank()) {
            throw QuestionValidationException("Question text cannot be empty", "question")
        }
        if (question.length < MIN_QUESTION_LENGTH) {
            throw QuestionValidationException(
                    "Question text must be at least $MIN_QUESTION_LENGTH characters long",
                    "question"
            )
        }
        if (question.length > MAX_QUESTION_LENGTH) {
            throw QuestionValidationException(
                    "Question text cannot exceed $MAX_QUESTION_LENGTH characters",
                    "question"
            )
        }
    }

    private fun validateMarks(marks: Int) {
        if (marks < MIN_MARKS) {
            throw QuestionValidationException("Question marks must be at least $MIN_MARKS", "marks")
        }
        if (marks > MAX_MARKS) {
            throw QuestionValidationException("Question marks cannot exceed $MAX_MARKS", "marks")
        }
    }

    private fun validateNegativeMarks(negativeMark: Int?, totalMarks: Int) {
        if (negativeMark != null) {
            if (negativeMark < 0) {
                throw QuestionValidationException(
                        "Negative marks cannot be negative",
                        "negativeMark"
                )
            }
            if (negativeMark > totalMarks) {
                throw QuestionValidationException(
                        "Negative marks cannot exceed total marks",
                        "negativeMark"
                )
            }
        }
    }

    private fun validateCO(co: Int) {
        if (co < 1 || co > 10) {
            throw QuestionValidationException("Course Outcome (CO) must be between 1 and 10", "co")
        }
    }

    private fun validateTopicIds(topicIds: List<UUID>) {
        if (topicIds.isEmpty()) {
            throw QuestionValidationException("At least one topic must be selected", "topicIds")
        }
        if (topicIds.size > 5) {
            throw QuestionValidationException("Cannot select more than 5 topics", "topicIds")
        }
    }

    private fun validateBloomsTaxonomy(@Suppress("UNUSED_PARAMETER") bloomsTaxonomy: Taxonomy) {
        // Enum validation is automatically handled, but we can add business logic if needed
        // For now, all enum values are valid
    }

    private fun validateDifficulty(@Suppress("UNUSED_PARAMETER") difficulty: Difficulty) {
        // Enum validation is automatically handled, but we can add business logic if needed
        // For now, all enum values are valid
    }

    private fun validateFunctionName(functionName: String) {
        if (functionName.length < MIN_CODING_FUNCTION_NAME_LENGTH) {
            throw QuestionValidationException(
                    "Function name must be at least $MIN_CODING_FUNCTION_NAME_LENGTH characters long",
                    "functionName"
            )
        }
        if (functionName.length > MAX_CODING_FUNCTION_NAME_LENGTH) {
            throw QuestionValidationException(
                    "Function name cannot exceed $MAX_CODING_FUNCTION_NAME_LENGTH characters",
                    "functionName"
            )
        }
        if (!functionName.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
            throw QuestionValidationException(
                    "Function name must be a valid identifier",
                    "functionName"
            )
        }
    }

    private fun validateReturnType(returnType: String) {
        if (returnType.isBlank()) {
            throw QuestionValidationException("Return type cannot be empty", "returnType")
        }
        if (returnType.length > 50) {
            throw QuestionValidationException(
                    "Return type cannot exceed 50 characters",
                    "returnType"
            )
        }
    }

    private fun validateDriverCode(driverCode: String) {
        if (driverCode.length > 10000) {
            throw QuestionValidationException(
                    "Driver code cannot exceed 10000 characters",
                    "driverCode"
            )
        }
    }

    private fun validateBoilerCode(boilerCode: String) {
        if (boilerCode.length > 5000) {
            throw QuestionValidationException(
                    "Boiler code cannot exceed 5000 characters",
                    "boilerCode"
            )
        }
    }
}
