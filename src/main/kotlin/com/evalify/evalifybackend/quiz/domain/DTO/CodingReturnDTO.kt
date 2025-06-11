package com.evalify.evalifybackend.quiz.domain.DTO

import com.evalify.evalifybackend.bank.domain.DTO.FunctionParamDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.question.domain.FunctionParam

class CodingReturnDTO(
    val question:String,
    val functionName: String?,
    val returnType: String?,
    val params: List<FunctionParamDTO>?,
    val language: List<String>?,
    val hintText: String?,
    val markValue: Int,
    val taxonomy: Taxonomy?,
    val coValue: Int,
    val difficultyLevel: Difficulty?
) : QuestionsReturnDTO(
    hint = hintText,
    marks = markValue,
    bloomsTaxonomy = taxonomy,
    co = coValue,
    difficulty = difficultyLevel


)