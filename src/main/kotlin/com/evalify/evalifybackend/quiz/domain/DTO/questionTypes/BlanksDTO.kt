package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import java.util.UUID

enum class BlankType{
    LOWERCASE, UPPERCASE, INTEGER, FLOAT, STRING

}

data class BlanksDTO(
    val sNo: Int,
    val id : Int,
    val type: BlankType,
)