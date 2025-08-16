package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import java.util.UUID

data class SetQuestionReturnDTO(
    val setId: UUID? = null,
    val questions: List<QuizQuestionsReturnDTO?>,
) {
}