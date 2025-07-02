package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ConflictException

/** Exception thrown when quiz set operations encounter conflicts */
class QuizSetException(quizId: String, setNumber: Int, message: String) :
        ConflictException("Quiz set error for quiz $quizId, set $setNumber: $message")
