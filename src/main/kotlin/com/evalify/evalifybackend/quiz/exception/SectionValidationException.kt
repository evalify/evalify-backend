package com.evalify.evalifybackend.quiz.exception

import com.evalify.evalifybackend.core.exception.ValidationException

/** Exception thrown when quiz section validation fails */
class SectionValidationException(message: String, field: String? = null) :
        ValidationException(message, field)
