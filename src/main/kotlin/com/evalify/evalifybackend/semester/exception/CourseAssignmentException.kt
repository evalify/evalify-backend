package com.evalify.evalifybackend.semester.exception

class CourseAssignmentException(message: String, cause: Throwable? = null) : RuntimeException("Course assignment error: $message", cause)
