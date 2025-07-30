package com.evalify.evalifybackend.bank.domain.DTO.crud

import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.PairDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.Pair
import com.evalify.evalifybackend.quiz.question.domain.FillUp.blanks
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import com.evalify.evalifybackend.quiz.question.domain.MatchPair
import com.evalify.evalifybackend.quiz.question.domain.Topic
import java.util.UUID

data class PatchQuestionDTO(
    val question: String? = null,
    val explanation: String? = null,
    val hint: String? = null,
    val marks: Int? = null,
    val negativeMark: Int? = null,
    val bloomsTaxonomy: Taxonomy? = null,
    val co: Int? = null,
    val difficulty: Difficulty? = null,
    val options: MutableList<MCQOption>? = null,
    val answer: String? = null,
    val link: String? = null,
    val driverCode: String? = null,
    val boilerCode: String? = null,
    val functionName: String? = null,
    val returnType: String? = null,
    val params: List<FunctionParamDTO>? = null,
    val testcases: List<TestCaseDTO>? = null,
    val language: List<String>? = null,
    val strictMatch: Boolean? = null,
    val llmEval: Boolean? = null,
    val template: String? = null,
    val blanks: List<blanks>? = null,
    val expectedAnswer: String? = null,
    val strictness: Float? = null,
    val guidelines: String? = null,
    val keys: MutableList<PairDTO>? = null,
    val values : MutableList<PairDTO>? = null,
    val matchPair : MutableList<MatchPair>? = null,
    val trueFalseAnswer: Boolean? = null,
    val topic : List<UUID>? = emptyList()
)