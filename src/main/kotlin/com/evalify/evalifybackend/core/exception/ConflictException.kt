package com.evalify.evalifybackend.core.exception

/** Exception thrown when an operation conflicts with the current state */
open class ConflictException(message: String) : RuntimeException(message)
