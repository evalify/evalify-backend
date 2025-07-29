package com.evalify.evalifybackend.quiz.domain.DTO.sharing

import com.evalify.evalifybackend.quiz.domain.QuizStatus
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class SharedQuizPreviewDTO(
    val id: UUID?,
    val name: String,
    val status: QuizStatus?,
    val isProtected: Boolean,
    val courseCodes : List<String>,
    val sharedWith : List<String?>
) {
}