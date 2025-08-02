package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz

import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.section.domain.Section

data class QuizQuestionsReturnDTO(
    val questions: QuestionsReturnDTO,
    val section : GetSectionDTO

) {
}