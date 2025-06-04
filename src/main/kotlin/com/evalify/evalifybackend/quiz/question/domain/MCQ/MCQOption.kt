package com.evalify.evalifybackend.quiz.question.domain.MCQ

import java.util.UUID

class MCQOption(
    val id: UUID,
    val text:String,
    val isCorrect: Boolean
) {
}