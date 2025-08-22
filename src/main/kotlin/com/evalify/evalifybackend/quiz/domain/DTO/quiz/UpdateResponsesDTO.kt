package com.evalify.evalifybackend.quiz.domain.DTO.quiz

data class UpdateResponsesDTO(
    val responses : List<Map<String,Any>>? = null
) {
}