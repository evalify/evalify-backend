package com.evalify.evalifybackend.bank.domain

import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.topic.domain.Topic
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "bank")
class Bank(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val name: String,
    val description: String,
    val courseCode: String,
    @ManyToOne(cascade = [CascadeType.ALL],fetch = FetchType.LAZY)
    val course : Course,
    @ManyToMany(fetch = FetchType.LAZY)
    val instructors : MutableList<User> = mutableListOf(),

    val createdAt: Instant? = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    val createdBy: User? = null,

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "bank", cascade = [CascadeType.ALL])
    val questions : MutableList<BankQuestion> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY)
    val topic : MutableList<Topic> = mutableListOf()
)