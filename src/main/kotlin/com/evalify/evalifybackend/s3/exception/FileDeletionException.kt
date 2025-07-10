package com.evalify.evalifybackend.s3.exception

class FileDeletionException(objectName: String, cause: Throwable? = null) : RuntimeException("Failed to delete file: $objectName", cause)
