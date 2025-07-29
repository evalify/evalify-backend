package com.evalify.evalifybackend.bank.domain.DTO.questionTypes

import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.BlankType
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.BlanksDTO

data class BlankDTO(
     val answers : List<String>,
    val type : BlankType
) {
}