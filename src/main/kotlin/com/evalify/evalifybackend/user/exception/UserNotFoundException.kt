package com.evalify.evalifybackend.user.exception

class UserNotFoundException(userId: String) : RuntimeException("User not found with ID: $userId")

class UsersNotFoundException(userIds: List<String>) : RuntimeException("Users not found with IDs: ${userIds.joinToString(", ")}")