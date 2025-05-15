package com.evalify.evalifybackend.quiz.domain

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
import kotlin.time.Duration

@Entity
@Table(name = "quiz")
class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    val name: String,
    val description: String? = null,
    val instructions: String? = null,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val password: String? = null,

    val fullScreen: Boolean = false,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false,
    val linearQuiz: Boolean = false,
    val calculator: Boolean = false,
    val autoSubmit: Boolean = false,
    val publishResult: Boolean = false,
    val publishQuiz: Boolean = false,

//    TODO: Add Relations btw Courses, Student, Lab, Class

    val createdAt: Instant  = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    val createdBy: User? = null
)