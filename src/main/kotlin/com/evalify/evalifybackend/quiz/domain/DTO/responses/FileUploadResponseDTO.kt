package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID
import kotlin.time.Duration

data class FileUploadResponseDTO(
    override val questionId: UUID,
    val answer: String? = null,
    override val duration: Duration

) : ResponseDTO(
    questionId = questionId,
    duration = duration
){
}