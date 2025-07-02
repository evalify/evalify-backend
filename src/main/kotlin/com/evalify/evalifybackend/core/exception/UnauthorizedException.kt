package com.evalify.evalifybackend.core.exception

/** Exception thrown when a user lacks authorization to perform an action */
open class UnauthorizedException(message: String = "Access denied") : RuntimeException(message)
