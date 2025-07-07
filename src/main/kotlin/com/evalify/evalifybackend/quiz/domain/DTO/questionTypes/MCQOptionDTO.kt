package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.util.UUID
import java.util.UUID.randomUUID

data class MCQOptionDTO(
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = randomUUID(),
    val text:String,

    )
