package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.lab.domain.Lab
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
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
    @ManyToMany
    @JoinTable(
        name = "course_quiz",
        joinColumns = [JoinColumn(name="course_id")],
        inverseJoinColumns = [JoinColumn(name="quiz_id")]
    )
    val course:MutableList<Course> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "quiz_student",
        joinColumns = [JoinColumn(name="quiz_id")],
        inverseJoinColumns = [JoinColumn(name="student_id")]
    )
    val student:MutableList<User> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "quiz_lab",
        joinColumns = [JoinColumn(name="lab_id")],
        inverseJoinColumns = [JoinColumn(name="quiz_id")]
    )
    val lab:MutableList<Lab>  = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "quiz_batch",
        joinColumns = [JoinColumn(name="quiz_id")],
        inverseJoinColumns = [JoinColumn(name="batch_id")]
    )
    val batch:MutableList<Batch> = mutableListOf(),

    val createdAt: Instant  = Instant.now(),
    @ManyToOne(fetch = FetchType.LAZY)
    val createdBy: User? = null

)