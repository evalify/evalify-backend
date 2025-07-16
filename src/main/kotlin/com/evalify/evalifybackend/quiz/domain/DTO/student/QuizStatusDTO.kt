package com.evalify.evalifybackend.quiz.domain.DTO.student

enum class QuizStatusDTO {
    UNKNOWN,
    COMPLETED,
    ACTIVE,
    UPCOMING,
    MISSED;

    companion object {
        fun from(value: String?): QuizStatusDTO {
            return try {
                valueOf(value?.uppercase() ?: "")
            } catch (e: Exception) {
                UNKNOWN
            }
        }
    }
}