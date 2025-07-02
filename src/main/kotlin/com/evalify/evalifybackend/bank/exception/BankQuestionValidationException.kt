package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.ValidationException

/** Exception thrown when bank question validation fails */
class BankQuestionValidationException(message: String, field: String? = null) :
        ValidationException(message, field)
