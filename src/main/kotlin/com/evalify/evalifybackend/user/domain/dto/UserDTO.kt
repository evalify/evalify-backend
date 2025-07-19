package com.evalify.evalifybackend.user.domain.dto

import com.evalify.evalifybackend.user.domain.Role
import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
    val id: String?,
    val name: String,
    val email: String,
    val profileId: String? = null,
    val image: String?,
    val role: Role,
    val password: String? = null,
    val phoneNumber: String,
    val isActive: Boolean,
    val createdAt: String
)

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: Role,
    val phoneNumber: String,
    val isActive: Boolean = true
)

data class UpdateUserRequest(
    val name: String,
    val email: String,
    val role: Role,
    val phoneNumber: String,
    val isActive: Boolean
)

data class UserInfo(
    val id: String?,
    val name: String,
    val email: String
)