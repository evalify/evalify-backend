package com.evalify.evalifybackend.user.exception

class UserValidationException(message: String) : RuntimeException("User validation failed: $message")
