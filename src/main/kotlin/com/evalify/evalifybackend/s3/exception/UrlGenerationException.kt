package com.evalify.evalifybackend.s3.exception

class UrlGenerationException(objectName: String, cause: Throwable? = null) : RuntimeException("Failed to generate URL for file: $objectName", cause)
