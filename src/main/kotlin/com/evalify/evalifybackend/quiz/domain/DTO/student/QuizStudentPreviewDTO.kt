package com.evalify.evalifybackend.quiz.domain.DTO.student

import com.evalify.evalifybackend.quiz.domain.QuizStatus
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class QuizStudentPreviewDTO(
    val id: UUID?,
    val name: String,
    val description: String?,
    val instructions: String?,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val status: QuizStatus, // Quiz status: UPCOMING, ACTIVE, ENDED
    val isProtected: Boolean,
    val courseCodes: List<String>,
    val isSubmitted: Boolean,
    val canStart: Boolean,
    val attemptsUsed: Int = 0,
    val maxAttempts: Int = 1,
    val remainingTime: Duration? = null
)


