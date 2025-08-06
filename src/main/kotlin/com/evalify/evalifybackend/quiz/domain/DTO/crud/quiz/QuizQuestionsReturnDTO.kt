package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz

import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.section.domain.Section
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class QuizQuestionsReturnDTO @JsonCreator constructor(
    @JsonProperty("questions") val questions: QuestionsReturnDTO,
    @JsonProperty("section") val section: GetSectionDTO,
    @JsonProperty("type") val type: QuestionTypes
)