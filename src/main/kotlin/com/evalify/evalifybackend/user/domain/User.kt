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
@Table(name = "\"user\"")
class User (
    @Id
    val id: String? = null,
    var name: String,
    var email: String,
    @Column(unique = true)
    var profileId: String? = null,
    var password: String? = null,
    var image: String? = null,
    @Enumerated(EnumType.STRING)
    var role: Role,
    var phoneNumber: String,
    var isActive: Boolean = true,
    var createdAt: Timestamp = Timestamp.from(Instant.now()),
    var lastPasswordChange: Timestamp? = null
)
