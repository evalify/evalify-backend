package com.evalify.evalifybackend.user.exception

class InvalidRoleException(role: String) : RuntimeException("Invalid role: $role")
