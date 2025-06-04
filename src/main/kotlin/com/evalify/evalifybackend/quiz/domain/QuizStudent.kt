package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.*
import kotlin.time.Duration
import java.time.Instant
import java.util.UUID

@Entity
@Table(name="quiz_student")
class QuizStudent(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @OneToOne
    val quiz: Quiz,

    @ManyToOne
    @JoinColumn(name = "student_id")
    val student: User,

    val startTime: Instant?,

    val duration: Duration,

    val endTime:Instant?,

    val isSubmitted: Boolean,

    val voilations: MutableList<String>? = mutableListOf(),

    val ipAddress: MutableList<String>,


    ) {
}