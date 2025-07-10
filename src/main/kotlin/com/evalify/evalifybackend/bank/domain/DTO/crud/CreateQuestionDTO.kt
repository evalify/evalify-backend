package com.evalify.evalifybackend.bank.domain.DTO.crud

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.BlankDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.MatchPairDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import java.util.UUID

data class CreateQuestionDTO(
    val type: QuestionTypes,
    val question: String,
    val topicIds: List<UUID>,
    val explanation: String?,
    val hint: String?,
    val marks: Int,
    val bloomsTaxonomy: Taxonomy,
    val co: Int,
    val negativeMark: Int?,
    val difficulty: Difficulty,

    val options: List<MCQOption>? = null,

    val driverCode: String? = null,
    val boilerCode: String? = null,
    val functionName: String,
    val returnType: String,
    val params: List<FunctionParamDTO>,
    val testcases: List<TestCaseDTO>,
    val language: List<String>,
    val answer: String? = null,


    val strictMatch: Boolean? = null,
    val llmEval: Boolean? = null,
    val template: String? = null,
    val blanks: List<BlankDTO>? = null,


    val expectedAnswer: String? = null,
    val strictness: Float? = null,
    val guidelines: String? = null,


    val keys: List<MatchPairDTO>? = null,


    val trueFalseAnswer: Boolean? = null
)