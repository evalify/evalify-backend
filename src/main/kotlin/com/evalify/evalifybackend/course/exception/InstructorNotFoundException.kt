package com.evalify.evalifybackend.course.exception

class InstructorNotFoundException(instructorId: String) : RuntimeException("Instructor with ID $instructorId not found")