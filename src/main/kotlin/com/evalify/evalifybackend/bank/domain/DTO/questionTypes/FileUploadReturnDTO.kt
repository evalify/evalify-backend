package com.evalify.evalifybackend.bank.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy

data class FileUploadReturnDTO(
    val question: String,
    override val hint: String?,
    override val marks: Int,
    override val bloomsTaxonomy: Taxonomy?,
    override val co: Int,
    override val difficulty: Difficulty?,
    val explanation : String?,
    val expectedAnswer:String?,
    val strictness:Float?,
    val guidelines:String?,
    val type : QuestionTypes?

) : BankQuestionsReturnDTO(
    hint = hint,
    marks = marks,
    bloomsTaxonomy = bloomsTaxonomy,
    co = co,
    difficulty = difficulty
){

}