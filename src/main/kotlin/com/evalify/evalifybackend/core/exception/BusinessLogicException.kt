package com.evalify.evalifybackend.core.exception

/** Exception thrown when business logic rules are violated */
open class BusinessLogicException(message: String) : RuntimeException(message)
