package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResultDTO
import com.evalify.evalifybackend.quiz.domain.DTO.studentResponses.StudentResponseDTO
import com.evalify.evalifybackend.user.domain.User
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

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

    val duration: Duration, // Duration in milliseconds

    val endTime:Instant?,

    val isSubmitted: Boolean,

    val violations: MutableList<String>? = mutableListOf(),

    val isViolated: Boolean? = false,

    val ipAddress: MutableList<String>,

    val submitTime: Instant? = null,

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var responses: MutableList<StudentResponseDTO> = mutableListOf(),


    @ElementCollection
    @CollectionTable(name = "quiz_student_results", joinColumns = [JoinColumn(name = "quiz_student_responses_id")])
    var results: MutableList<ResultDTO> = mutableListOf(),


    var setNumber : Int = 1
 ) {
}