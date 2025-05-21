package com.evalify.evalifybackend.answer

data class MMCQOption(val text: String, val isCorrect: Boolean)

data class MMCQ(val options: List<MMCQOption>) : QuestionAnswer