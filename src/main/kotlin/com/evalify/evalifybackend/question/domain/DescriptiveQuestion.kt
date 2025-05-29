package com.evalify.evalifybackend.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity


@Entity
@DiscriminatorValue(value = "DESCRIPTIVE")
class DescriptiveQuestion(
    question: String = "",
    bank: Bank,
    topic: MutableList<Topic>,
    explanation: String? = "",
    hint: String? = "",
    marks: Int,
    bloomsTaxonomy: Taxonomy,
    co: Int,
    negativeMark: Int? = null,
    difficulty: Difficulty,
    val expectedAnswer:String?,
    val strictness:Float?,
    val guidelines:String?
) : BaseQuestion(
    question = question,
    bank = bank,
    topic = topic,
    explanation = explanation,
    hint = hint,
    marks = marks,
    bloomsTaxonomy = bloomsTaxonomy,
    co = co,
    negativeMark = negativeMark,
    difficulty = difficulty
){
}