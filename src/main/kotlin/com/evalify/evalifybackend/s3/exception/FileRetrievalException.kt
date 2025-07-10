package com.evalify.evalifybackend.s3.exception

class FileRetrievalException(objectName: String, cause: Throwable? = null) : RuntimeException("Failed to retrieve file: $objectName", cause)
