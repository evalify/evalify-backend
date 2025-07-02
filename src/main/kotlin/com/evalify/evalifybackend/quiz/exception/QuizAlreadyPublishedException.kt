package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when attempting to publish a quiz that is already published */
class QuizAlreadyPublishedException(quizId: String) :
        ConflictException("Quiz $quizId is already published")
