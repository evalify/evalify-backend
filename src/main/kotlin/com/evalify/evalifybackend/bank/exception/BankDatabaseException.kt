package com.evalify.evalifybackend.bank.exception

import com.evalify.evalifybackend.core.exception.ExternalServiceException

/** Exception thrown when database operations fail */
class BankDatabaseException(operation: String, cause: Throwable? = null) :
        ExternalServiceException("Database operation '$operation' failed", "bank-database", cause)
