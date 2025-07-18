package com.evalify.evalifybackend.quiz.question.domain.MCQ

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.util.UUID
import java.util.UUID.randomUUID

class MCQOption(

    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = randomUUID(),
    val text:String,
    val isCorrect: Boolean
) {
}