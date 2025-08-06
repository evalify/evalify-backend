package com.evalify.evalifybackend.quiz.domain.DTO.quiz

import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class QuizQuestionResponseDTO @JsonCreator constructor(
    @JsonProperty("question") val question: QuizQuestionsReturnDTO,
    @JsonProperty("response") val response: ResponseDTO? = null
) {
}