package com.evalify.evalifybackend.bank.domain.DTO.questionTypes

import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import java.util.UUID

data class DescriptiveBankReturnDTO(
    val question: String,
    override val questionId : UUID?,
    override val hint: String?,
    override val marks: Int,
    override val bloomsTaxonomy: Taxonomy?,
    override val co: Int,
    override val difficulty: Difficulty?,
    val explanation : String?,
    val expectedAnswer:String?,
    val strictness:Float?,
    val guidelines:String?,
    val answer : String?,
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

){

}