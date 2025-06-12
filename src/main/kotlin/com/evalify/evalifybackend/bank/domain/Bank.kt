package com.evalify.evalifybackend.bank.domain

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinTable
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
    val courseCode: String? = null,
    val semester: Int,

    @OneToMany(fetch = FetchType.LAZY , mappedBy = "bank")
    val topics : MutableList<Topic>? = mutableListOf(),


    @ManyToMany
    val sharedUser:MutableList<User> = mutableListOf(),

    val createdAt: Instant,
    @ManyToOne(fetch = FetchType.LAZY)
    val createdBy: User,

    @ManyToMany
    val bankQuestion: MutableList<BankQuestion> = mutableListOf()
)