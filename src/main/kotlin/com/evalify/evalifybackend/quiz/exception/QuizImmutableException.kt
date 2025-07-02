package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when attempting to modify immutable quiz properties */
class QuizImmutableException(quizId: String, property: String) :
        ConflictException("Cannot modify immutable property '$property' of quiz $quizId")
