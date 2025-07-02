package com.evalify.evalifybackend.core.exception

/** Exception thrown when input validation fails */
open class ValidationException(message: String, val field: String? = null) :
        RuntimeException(message)
