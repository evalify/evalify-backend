package com.evalify.evalifybackend.section.exception

class SectionValidationException(message: String) : RuntimeException("Section validation failed: $message")
