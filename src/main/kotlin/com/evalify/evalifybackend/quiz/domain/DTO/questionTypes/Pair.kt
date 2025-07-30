package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import java.util.UUID
import java.util.UUID.randomUUID

data class Pair(
    val id: UUID = randomUUID(),
    val text:String

    ) {
}