package com.evalify.evalifybackend.answer

data class MCQOption(val option: String, val isCorrect: Boolean)

data class MCQ(val options: List<MCQOption>) : QuestionAnswer