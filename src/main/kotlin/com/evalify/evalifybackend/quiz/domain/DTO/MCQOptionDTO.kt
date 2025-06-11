package com.evalify.evalifybackend.quiz.domain.DTO

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.util.UUID

data class MCQOptionDTO(
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val text:String,

    )
