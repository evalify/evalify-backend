package com.evalify.evalifybackend.batch.exception

import com.evalify.evalifybackend.core.exception.ValidationException

/** Exception thrown when batch validation fails */
class BatchValidationException(message: String, field: String? = null) :
        ValidationException(message, field)