package com.evalify.evalifybackend.bank.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import java.util.UUID

class CodingBankReturnDTO(
    val question:String,
    override val questionId : UUID?,
    val language: List<String>?,
    override val hint: String?,
    override val marks: Int,
    override val bloomsTaxonomy: Taxonomy?,
    override val co: Int,
    override val difficulty: Difficulty?,
    val driverCode: String?,
    val boilerCode: String?,
    val testcases: List<TestCaseDTO>?,
    val answer: String?,
    val type : QuestionTypes?,
    override val topics: List<ReturnTopicDTO>
) : BankQuestionsReturnDTO(
    hint = hint,
    marks = marks,
    bloomsTaxonomy = bloomsTaxonomy,
    co = co,
    difficulty = difficulty,
    topics = topics,
    questionId = questionId


)