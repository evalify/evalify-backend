package com.evalify.evalifybackend.user.exception

class UserServiceException(message: String, cause: Throwable? = null) : RuntimeException("User service error: $message", cause)
