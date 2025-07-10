package com.evalify.evalifybackend.semester.exception

class SemesterServiceException(message: String, cause: Throwable? = null) : RuntimeException("Semester service error: $message", cause)
