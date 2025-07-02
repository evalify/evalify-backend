package com.evalify.evalifybackend.quiz.util

import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.exception.QuizAccessDeniedException
import com.evalify.evalifybackend.quiz.exception.QuizStateException
import com.evalify.evalifybackend.quiz.exception.QuizTimingException
import java.time.Instant

/** Utility class for quiz security operations and access control */
object QuizSecurityUtils {

    /** Checks if a user has access to a quiz */
    fun checkQuizAccess(
            quiz: Quiz,
            userId: String,
            requiredAccess: SharedTags = SharedTags.SHARED
    ): Boolean {
        return quiz.sharedUsers.any { quizUser ->
            quizUser.user?.id == userId && hasRequiredAccess(quizUser.tags, requiredAccess)
        }
    }

    /** Ensures user has access to a quiz, throws exception if not */
    fun ensureQuizAccess(
            quiz: Quiz,
            userId: String,
            requiredAccess: SharedTags = SharedTags.SHARED
    ) {
        if (!checkQuizAccess(quiz, userId, requiredAccess)) {
            throw QuizAccessDeniedException(quiz.id.toString(), userId)
        }
    }

    /** Checks if user is owner of the quiz */
    fun isOwner(quiz: Quiz, userId: String): Boolean {
        return checkQuizAccess(quiz, userId, SharedTags.OWNER)
    }

    /** Ensures user is owner of the quiz */
    fun ensureOwnership(quiz: Quiz, userId: String) {
        ensureQuizAccess(quiz, userId, SharedTags.OWNER)
    }

    /** Gets user's access level for a quiz */
    fun getUserAccessLevel(quiz: Quiz, userId: String): SharedTags? {
        return quiz.sharedUsers.find { it.user?.id == userId }?.tags
    }

    /** Validates quiz timing constraints */
    fun validateQuizTiming(quiz: Quiz, operation: String) {
        val now = Instant.now()

        when (operation) {
            "START" -> {
                if (now.isBefore(quiz.startTime)) {
                    throw QuizTimingException(
                            quiz.id.toString(),
                            "Quiz has not started yet. Start time: ${quiz.startTime}"
                    )
                }
                if (now.isAfter(quiz.endTime)) {
                    throw QuizTimingException(
                            quiz.id.toString(),
                            "Quiz has already ended. End time: ${quiz.endTime}"
                    )
                }
            }
            "SUBMIT" -> {
                if (now.isAfter(quiz.endTime)) {
                    throw QuizTimingException(
                            quiz.id.toString(),
                            "Quiz submission time has expired. End time: ${quiz.endTime}"
                    )
                }
            }
            "EDIT" -> {
                if (quiz.publishQuiz && now.isAfter(quiz.startTime)) {
                    throw QuizStateException(quiz.id.toString(), "edit", "published and started")
                }
            }
            "DELETE" -> {
                if (quiz.publishQuiz && now.isAfter(quiz.startTime)) {
                    throw QuizStateException(quiz.id.toString(), "delete", "published and started")
                }
            }
        }
    }

    /** Validates quiz state for specific operations */
    fun validateQuizState(quiz: Quiz, operation: String) {
        when (operation) {
            "PUBLISH" -> {
                if (quiz.publishQuiz) {
                    throw QuizStateException(quiz.id.toString(), "publish", "already published")
                }
                if (quiz.section.isEmpty()) {
                    throw QuizStateException(quiz.id.toString(), "publish", "no sections")
                }
                if (quiz.section.all { it.quizQuestions.isEmpty() }) {
                    throw QuizStateException(quiz.id.toString(), "publish", "no questions")
                }
            }
            "UNPUBLISH" -> {
                if (!quiz.publishQuiz) {
                    throw QuizStateException(quiz.id.toString(), "unpublish", "not published")
                }
                val now = Instant.now()
                if (now.isAfter(quiz.startTime)) {
                    throw QuizStateException(quiz.id.toString(), "unpublish", "already started")
                }
            }
            "ADD_QUESTIONS" -> {
                if (quiz.publishQuiz) {
                    val now = Instant.now()
                    if (now.isAfter(quiz.startTime)) {
                        throw QuizStateException(
                                quiz.id.toString(),
                                "add questions",
                                "published and started"
                        )
                    }
                }
            }
            "REMOVE_QUESTIONS" -> {
                if (quiz.publishQuiz) {
                    val now = Instant.now()
                    if (now.isAfter(quiz.startTime)) {
                        throw QuizStateException(
                                quiz.id.toString(),
                                "remove questions",
                                "published and started"
                        )
                    }
                }
            }
        }
    }

    /** Checks if a user is a student assigned to the quiz */
    fun isAssignedStudent(quiz: Quiz, userId: String): Boolean {
        return quiz.student.any { it.id == userId }
    }

    /** Ensures user is an assigned student */
    fun ensureAssignedStudent(quiz: Quiz, userId: String) {
        if (!isAssignedStudent(quiz, userId)) {
            throw QuizAccessDeniedException(quiz.id.toString(), userId)
        }
    }

    /** Validates quiz permissions for specific operations */
    fun validateQuizPermissions(quiz: Quiz, userId: String, operation: String) {
        when (operation) {
            "READ" -> {
                ensureQuizAccess(quiz, userId, SharedTags.SHARED)
            }
            "WRITE" -> {
                ensureQuizAccess(quiz, userId, SharedTags.SHARED)
            }
            "DELETE", "SHARE", "UNSHARE" -> {
                ensureOwnership(quiz, userId)
            }
            "TAKE_QUIZ" -> {
                ensureAssignedStudent(quiz, userId)
                validateQuizTiming(quiz, "START")
            }
            "SUBMIT_QUIZ" -> {
                ensureAssignedStudent(quiz, userId)
                validateQuizTiming(quiz, "SUBMIT")
            }
        }
    }

    private fun hasRequiredAccess(userAccess: SharedTags, requiredAccess: SharedTags): Boolean {
        return when (requiredAccess) {
            SharedTags.OWNER -> userAccess == SharedTags.OWNER
            SharedTags.SHARED -> userAccess == SharedTags.OWNER || userAccess == SharedTags.SHARED
        }
    }
}
