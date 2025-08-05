package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import java.time.Instant
import kotlin.time.Duration

data class QuizStudentInfoDTO(
    val duration : Duration?,
    val endTime : Instant?,
    val startTime : Instant?,
    val violations : MutableList<String>?,
    val isViolated:Boolean?

    ) {
}