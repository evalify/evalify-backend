package com.evalify.evalifybackend.bank.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy

class TrueFalseBankDTO(
    val question:String,
    val answers : Boolean,
    val hintText: String?,
    val markValue: Int,
    val taxonomy: Taxonomy?,
    val coValue: Int,
    val difficultyLevel: Difficulty?,
    val explanation: String?,
    val type : QuestionTypes?
) : BankQuestionsReturnDTO(
    hint = hintText,
    marks = markValue,
    bloomsTaxonomy = taxonomy,
    co = coValue,
    difficulty = difficultyLevel
)