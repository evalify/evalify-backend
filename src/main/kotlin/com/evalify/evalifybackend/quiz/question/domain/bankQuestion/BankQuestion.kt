package com.evalify.evalifybackend.quiz.question.domain.bankQuestion

import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "bank_question")
class BankQuestion (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,

    @OneToOne
    val question: BaseQuestion,

    val updatedAt: Instant = Instant.now(),

    @ManyToOne
    val updateBy: User

)