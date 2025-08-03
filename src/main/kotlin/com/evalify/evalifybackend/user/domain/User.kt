package com.evalify.evalifybackend.user.domain

import com.evalify.evalifybackend.quiz.domain.Quiz
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
    val name: String,
    val email: String,
    @Column(unique = true)
    val profileId: String? = null,
    val password: String? = null,
    val image: String? = null,
    //@Enumerated(EnumType.STRING)
    val role: Role,
    val phoneNumber: String,
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.from(Instant.now()),
    val lastPasswordChange: Timestamp? = null,

    @ManyToMany(mappedBy = "student")
    val quiz: MutableList<Quiz> = mutableListOf()


    )
