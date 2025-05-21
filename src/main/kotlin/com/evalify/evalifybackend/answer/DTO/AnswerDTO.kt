package com.evalify.evalifybackend.answer.DTO

import com.evalify.evalifybackend.answer.QuestionAnswer

data class AnswerDTO(
    val type: String,
    val answer: QuestionAnswer
)
