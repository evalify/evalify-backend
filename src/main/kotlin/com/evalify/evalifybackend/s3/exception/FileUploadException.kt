package com.evalify.evalifybackend.s3.exception

class FileUploadException(message: String, cause: Throwable? = null) : RuntimeException("Failed to upload file: $message", cause)
