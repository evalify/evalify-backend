package com.evalify.evalifybackend.quiz.question.domain.MCQ

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.util.UUID

class MCQOption(
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val text:String,
    val isCorrect: Boolean
) {
}