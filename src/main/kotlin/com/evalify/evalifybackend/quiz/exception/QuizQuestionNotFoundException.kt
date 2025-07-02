package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/** Exception thrown when a quiz question is not found */
class QuizQuestionNotFoundException(questionId: String) :
        NotFoundException("Quiz question with ID $questionId not found")
