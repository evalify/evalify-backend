package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.question.domain.FillUp.blanks
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQOption
import com.evalify.evalifybackend.quiz.question.domain.MatchPair
import com.evalify.evalifybackend.quiz.question.domain.Topic

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
    val keys: MutableList<MatchPair>? = null,
    val trueFalseAnswer: Boolean? = null,
    val topic : MutableList<Topic>? = mutableListOf()
)
