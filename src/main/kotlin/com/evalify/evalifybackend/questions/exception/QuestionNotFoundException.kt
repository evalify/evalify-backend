package com.evalify.evalifybackend.questions.exception

import com.evalify.evalifybackend.core.exception.NotFoundException

/** Exception thrown when a question is not found */
class QuestionNotFoundException(questionId: String) :
        NotFoundException("Question with ID $questionId not found")
