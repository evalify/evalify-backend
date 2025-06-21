package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class QuizUpdateDTO(
    val name: String? = null,
    val description: String? = null,
    val instructions: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val duration: Duration? = null,
    val password: String? = null,

    val fullScreen: Boolean? = null,
    val shuffleQuestions: Boolean? = null,
    val shuffleOptions: Boolean? = null,
    val linearQuiz: Boolean? = null,
    val calculator: Boolean? = null,
    val autoSubmit: Boolean? = null,
    val publishResult: Boolean? = null,
    val publishQuiz: Boolean? = null,

)
