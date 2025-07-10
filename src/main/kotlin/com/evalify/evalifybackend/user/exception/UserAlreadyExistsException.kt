package com.evalify.evalifybackend.user.exception

class UserAlreadyExistsException(email: String) : RuntimeException("User with email '$email' already exists")
