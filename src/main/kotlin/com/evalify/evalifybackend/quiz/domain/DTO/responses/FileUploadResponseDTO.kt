package com.evalify.evalifybackend.quiz.domain.DTO.responses

import java.util.UUID

data class FileUploadResponseDTO(
    override val questionId: UUID,
    val url: String
) : ResponseDTO(
    questionId = questionId
){
}