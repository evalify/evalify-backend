package com.evalify.evalifybackend.quiz.domain.DTO.student

import com.evalify.evalifybackend.quiz.domain.QuizStatus
import java.time.Duration
import java.time.Instant

data class PreviewQuizDTO(
    val title: String,
    val description: String,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val quizStatus: QuizStatus
) {

}