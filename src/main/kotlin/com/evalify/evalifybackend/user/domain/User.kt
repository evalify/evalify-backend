package com.evalify.evalifybackend.user.domain

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

enum class Role {
    STUDENT,
    ADMIN,
    FACULTY,
    MANAGER
}

@Entity
@Table(name = "users")
class User (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val name: String,
    val email: String,
    val profileId: String,
    val image: String? = null,
    val role: Role,
    val phoneNumber: String,
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.from(Instant.now()),
    val lastPasswordChange: Timestamp? = null
)
