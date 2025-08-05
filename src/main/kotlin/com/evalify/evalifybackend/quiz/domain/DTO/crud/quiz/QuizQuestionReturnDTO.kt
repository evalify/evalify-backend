package com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz

import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizTagDTO
import com.evalify.evalifybackend.quiz.domain.QuizTags

data class QuizQuestionReturnDTO @JsonCreator constructor(
    @JsonProperty("quizTags") val quizTags: List<QuizTagsReturnDTO>,
    @JsonProperty("questions") val questions: List<QuizQuestionsReturnDTO>,
    @JsonProperty("message") val message: String? = null
)