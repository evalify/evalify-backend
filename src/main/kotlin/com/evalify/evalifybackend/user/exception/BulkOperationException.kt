package com.evalify.evalifybackend.user.exception

class BulkOperationException(message: String, cause: Throwable? = null) : RuntimeException("Bulk operation failed: $message", cause)
