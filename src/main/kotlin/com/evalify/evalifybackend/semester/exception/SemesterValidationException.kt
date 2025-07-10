package com.evalify.evalifybackend.semester.exception

class SemesterValidationException(message: String) : RuntimeException("Semester validation failed: $message")
