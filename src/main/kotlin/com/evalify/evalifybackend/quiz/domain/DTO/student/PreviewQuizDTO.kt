package com.evalify.evalifybackend.quiz.domain.DTO.student

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.domain.QuizTags
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class PreviewQuizDTO(
    val id: UUID? = null,
    val name: String,
    val description: String?,
    val startTime: Instant,
    val endTime: Instant,
    val duration: Duration,
    val status: QuizStatusDTO,
    val quizTags: List<QuizTagsReturnDTO>,
    val instructions: String?,
    val linearQuiz:Boolean?,
    val protected:Boolean?,
) {

}