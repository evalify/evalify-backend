package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when attempting to share a quiz with a user who already has access */
class QuizAlreadySharedException(quizId: String, userId: String) :
        ConflictException("Quiz $quizId is already shared with user $userId")
