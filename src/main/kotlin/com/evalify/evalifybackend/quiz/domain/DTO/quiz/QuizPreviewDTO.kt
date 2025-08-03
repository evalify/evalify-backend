package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import com.evalify.evalifybackend.quiz.domain.QuizStatus
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
    val publishResult: Boolean,
    val status: QuizStatus?,
    val isProtected: Boolean,
    val courseCodes : List<CourseInfoDTO>,
    val isPublished: Boolean


) {
}

data class CourseInfoDTO(
    val id: UUID?,
    val name: String,
    val courseCode: String
)