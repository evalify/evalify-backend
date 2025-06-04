package com.evalify.evalifybackend.quiz.domain.DTO

import java.time.Instant
import java.util.UUID

data class QuizStudentDTO(
    val id: UUID,
    val name:String,
    val description:String?,
    val instructions:String?,
    val endTime: Instant,
    val startTime: Instant,
    val fullScreen: Boolean,
    val shuffleQuestions: Boolean ,
    val shuffleOptions: Boolean,
    val linearQuiz: Boolean,
    val calculator: Boolean,
    val autoSubmit: Boolean
    )