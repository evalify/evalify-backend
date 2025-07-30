package com.evalify.evalifybackend.quiz.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.PairDTO

data class MatchShuffleDTO(

    val left: MutableList<PairDTO>?,
    val right: MutableList<PairDTO>?
)