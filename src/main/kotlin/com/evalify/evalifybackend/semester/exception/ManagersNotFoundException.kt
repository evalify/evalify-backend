package com.evalify.evalifybackend.semester.exception

class ManagersNotFoundException(message: String) : RuntimeException("Managers not found: $message")
