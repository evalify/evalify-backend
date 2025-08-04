package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResultDTO
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

    val violations: MutableList<String>? = mutableListOf(),

    val isViolated: Boolean? = false,

    val ipAddress: MutableList<String>,

    @ElementCollection
    @CollectionTable(name = "quiz_student_responses", joinColumns = [JoinColumn(name = "quiz_student_id")])
    var responses: MutableList<ResponseDTO> = mutableListOf(),


    @ElementCollection
    @CollectionTable(name = "quiz_student_results", joinColumns = [JoinColumn(name = "quiz_student_responses_id")])
    var results: MutableList<ResultDTO> = mutableListOf(),


    var setNumber : Int = 1
 ) {
}