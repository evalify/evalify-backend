package com.evalify.evalifybackend.quiz.domain.dto

import java.time.Instant
import java.util.UUID

data class CreateQuizDTO(
    val name: String,
    val description: String? = null,
    val instructions: String? = null,
    val startTime: Instant,
    val endTime: Instant,
    val durationInMinutes: Long,

    val fullScreen: Boolean = false,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false,
    val linearQuiz: Boolean = false,
    val calculator: Boolean = false,
    val autoSubmit: Boolean = false,
    //val publishResult: Boolean = false,
    //val publishQuiz: Boolean = false,

    val courseIds: List<UUID>,
    val studentIds: MutableList<String> = mutableListOf(),
    val labIds: List<UUID>,
    val batchIds: List<UUID>,

    val createdById: String? = null
)
