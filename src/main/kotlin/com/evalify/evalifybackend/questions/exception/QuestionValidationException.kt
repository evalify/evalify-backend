package com.evalify.evalifybackend.questions.exception

import com.evalify.evalifybackend.core.exception.ValidationException

/** Exception thrown when question validation fails */
class QuestionValidationException(message: String, field: String? = null) :
        ValidationException(message, field)
