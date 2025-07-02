package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/** Exception thrown when a quiz is not found */
class QuizNotFoundException(quizId: String) : NotFoundException("Quiz with ID $quizId not found")
