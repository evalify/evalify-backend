package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.ValidationException

/**
 * Exception thrown when bank validation fails
 */
class BankValidationException(message: String, field: String? = null) : ValidationException(message, field)
