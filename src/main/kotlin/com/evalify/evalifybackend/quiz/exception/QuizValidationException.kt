package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ValidationException

/** Exception thrown when quiz validation fails */
class QuizValidationException(message: String, field: String? = null) :
        ValidationException(message, field)
