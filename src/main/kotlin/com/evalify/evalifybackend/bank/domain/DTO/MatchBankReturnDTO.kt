package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.MatchShuffleDTO
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.MatchPair

class MatchBankReturnDTO (
    val question:String,
    val hintText: String?,
    val keys:MutableList<MatchPair>,
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