package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import java.util.UUID

data class BankBlanksDTO(
    val sNo: Int,
    val id : UUID?,
    val type: BlankType,
    val answers: List<String>
) {
}