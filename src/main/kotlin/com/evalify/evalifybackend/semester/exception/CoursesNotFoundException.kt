package com.evalify.evalifybackend.semester.exception

class CoursesNotFoundException(message: String) : RuntimeException("Courses not found: $message")
