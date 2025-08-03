package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.batch.domain.Batch
import com.evalify.evalifybackend.course.domain.Course
import com.evalify.evalifybackend.lab.domain.Lab
import com.evalify.evalifybackend.section.domain.Section
import com.evalify.evalifybackend.user.domain.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration



@Entity
@Table(name = "quiz")
class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    val id: UUID? = null,
    val name: String,
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    @Column(columnDefinition = "TEXT")
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

//    @OneToMany(mappedBy = "quiz", cascade = [CascadeType.ALL])
//    val quizQuestion: MutableList<QuizQuestion> = mutableListOf(),
    @OneToMany(mappedBy = "quiz", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val section:MutableList<Section> = mutableListOf(),

//    TODO: Add Relations btw Courses, Student, Lab, Class
    @ManyToMany
    @JoinTable(
        name = "course_quiz",
        joinColumns = [JoinColumn(name="quiz_id")],
        inverseJoinColumns = [JoinColumn(name="course_id")]
    )
    val course:MutableList<Course> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "student_quiz",
        joinColumns = [JoinColumn(name="quiz_id")],
        inverseJoinColumns = [JoinColumn(name="student_id")]
    )
    val student:MutableList<User> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "lab_quiz",
        joinColumns = [JoinColumn(name="quiz_id")],
        inverseJoinColumns = [JoinColumn(name="lab_id")]
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
    var noOfSets : Int = 1,
    @ManyToMany
    @JoinTable(
        name = "quiz_quizTags",
        joinColumns = [JoinColumn(name="quiz_id")],
        inverseJoinColumns = [JoinColumn(name="quizTag_id")]
    )
    val quizTags: MutableList<QuizTags> = mutableListOf(),

    @OneToMany(mappedBy = "quiz", cascade = [CascadeType.ALL], orphanRemoval = true)
    val sharedUsers: MutableList<QuizUser> = mutableListOf()
) {


    fun publishQuiz(noOfSets: Int) : Quiz {
        val quiz = Quiz(
            id = this.id,
            name = this.name,
            description = this.description,
            instructions = this.instructions,
            startTime = this.startTime,
            endTime = this.endTime,
            duration = this.duration,
            password = this.password,
            fullScreen = this.fullScreen,
            shuffleQuestions = this.shuffleQuestions,
            shuffleOptions = this.shuffleOptions,
            linearQuiz = this.linearQuiz,
            calculator = this.calculator,
            autoSubmit = this.autoSubmit,
            publishResult = this.publishResult,
            publishQuiz = true,
            section = this.section,
            course = this.course,
            student = this.student,
            lab = this.lab,
            batch = this.batch,
            createdAt = this.createdAt,
            noOfSets = noOfSets,
            sharedUsers = this.sharedUsers
        )

        return quiz
    }


}