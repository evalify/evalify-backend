package com.evalify.evalifybackend.answer

data class FillUp(
    val no_blanks: Int,
    val strictMatch: Boolean,
    val llmEval: Boolean,
    val template: String,
    val blanks: List<List<String>>
) : QuestionAnswer