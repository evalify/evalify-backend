package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class QuizPreviewDTO(
    val id: UUID?,
    val name: String,
    val description: String,
    val startTime: Instant,
    val endTime:Instant,
    val batches: List<String>,
    val labs:List<String>,
    val duration: Duration,
    val publishResult: Boolean


) {
}