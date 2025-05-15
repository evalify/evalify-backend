package com.evalify.evalifybackend.bank.domain

import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "bank")
class Bank(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name: String,
    val description: String,
    val semester: Int,
//    TODO: Course, User


    val createdAt: Instant,
    @ManyToOne(fetch = FetchType.LAZY)
    val createdBy: User? = null,
)