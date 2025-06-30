package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz



import java.time.Instant
import java.util.UUID

data class PatchQuizDTO(
    val name: String? = null,
    val description: String? = null,
    val instructions: String? = null,
    val startTime: Instant? = null,
    val endTime: Instant? = null,
    val durationInMinutes: Long? = null,
    val fullScreen: Boolean? = null,
    val shuffleQuestions: Boolean? = null,
    val shuffleOptions: Boolean? = null,
    val linearQuiz: Boolean? = null,
    val calculator: Boolean? = null,
    val autoSubmit: Boolean? = null,
    val publishResult: Boolean? = null,
    val publishQuiz: Boolean? = null,

    val courseIds: List<UUID>? = null,
    val batchIds: List<UUID>? = null,
    val studentIds: List<String>? = null,
    val labIds: List<UUID>? = null
)
