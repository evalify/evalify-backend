package com.evalify.evalifybackend.section.exception

class SectionServiceException(message: String, cause: Throwable? = null) : RuntimeException("Section service error: $message", cause)
