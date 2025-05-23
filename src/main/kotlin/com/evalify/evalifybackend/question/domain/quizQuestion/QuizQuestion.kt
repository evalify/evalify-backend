package com.evalify.evalifybackend.question.domain.quizQuestion

import com.evalify.evalifybackend.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "quiz_question")
class QuizQuestion (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID?=null,

    @OneToOne
    val question: BaseQuestion,

    @ManyToOne
    val quiz: Quiz,

    val updatedAt: Instant = Instant.now(),

    @ManyToOne
    val updateBy: User,

    @ManyToOne
    @JoinColumn(unique = true)
    val bankQuestion: BankQuestion

)
