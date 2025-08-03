package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedUserDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SimpleUserDTO
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.user.domain.dto.UserInfo
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration

data class GetQuizDTO(
    val id: UUID?,
    val name: String,
    val description: String,
    val startTime: Instant,
    val endTime:Instant,
    val batches: List<BatchInfoDTO>? = listOf(),
    val labs:List<LabInfoDTO>? = listOf(),
    val duration: Duration,
    val publishResult: Boolean,
    val status: QuizStatus?,
    val isProtected: Boolean,
    val courseCodes : List<CourseInfoDTO>? = listOf(),
    val isPublished: Boolean,
    val students : List<SimpleUserDTO>? = listOf(),
    val instructions: String? = null,
    val fullScreen: Boolean = false,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false,
    val linearQuiz: Boolean = false,
    val calculator: Boolean = false,
    val autoSubmit: Boolean = false,
    val quizTags : List<QuizTagDTO>? = listOf(),
    val createdAt: Instant,
    val noOfSets : Int,
    val owner : SimpleUserDTO,
    val sharedWith : List<SharedUserDTO>? = listOf(),

    ) {
}