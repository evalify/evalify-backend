package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO

data class DescriptiveBankReturnDTO(
    val question: String,
    val hintText: String?,
    val markValue: Int,
    val taxonomy: Taxonomy?,
    val coValue: Int,
    val difficultyLevel: Difficulty?,
    val explanation : String?,
    val expectedAnswer:String?,
    val strictness:Float?,
    val guidelines:String?,
    val answer : String?,
    val type : QuestionTypes?
) : BankQuestionsReturnDTO(
    hint = hintText,
    marks = markValue,
    bloomsTaxonomy = taxonomy,
    co = coValue,
    difficulty = difficultyLevel
){

}