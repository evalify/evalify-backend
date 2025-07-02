package com.evalify.evalifybackend.core.exception

/** Exception thrown when external service operations fail */
open class ExternalServiceException(
        message: String,
        val service: String? = null,
        cause: Throwable? = null
) : RuntimeException(message, cause)
