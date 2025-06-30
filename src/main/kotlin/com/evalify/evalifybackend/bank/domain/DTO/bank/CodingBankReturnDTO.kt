package com.evalify.evalifybackend.bank.domain.DTO.bank

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy

class CodingBankReturnDTO(
    val question:String,
    val functionName: String?,
    val returnType: String?,
    val params: List<FunctionParamDTO>?,
    val language: List<String>?,
    val hintText: String?,
    val markValue: Int,
    val taxonomy: Taxonomy?,
    val coValue: Int,
    val difficultyLevel: Difficulty?,
    val driverCode: String?,
    val boilerCode: String?,
    val testcases: List<TestCaseDTO>?,
    val answer: String?,
    val type : QuestionTypes?
) : BankQuestionsReturnDTO(
    hint = hintText,
    marks = markValue,
    bloomsTaxonomy = taxonomy,
    co = coValue,
    difficulty = difficultyLevel


)