package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.user.domain.dto.UserInfo
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class GetQuizPreviewDTO(
    val id: UUID?,
    val name: String,
    val description: String,
    val startTime: Instant,
    val endTime:Instant,
    val batches: List<BatchInfoDTO>,
    val labs:List<LabInfoDTO>,
    val duration: Duration,
    val publishResult: Boolean,
    val status: QuizStatus?,
    val isProtected: Boolean,
    val courseCodes : List<CourseInfoDTO>,
    val isPublished: Boolean,
    val students : List<UserInfo>
) {

}