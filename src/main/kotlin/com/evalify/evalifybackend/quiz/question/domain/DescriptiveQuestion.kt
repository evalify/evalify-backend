package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID


@Entity
@DiscriminatorValue(value = "DESCRIPTIVE")
class DescriptiveQuestion(
    id: UUID?,
    question: String = "",
    bank: Bank?,
//    topic: MutableList<Topic>,
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
    id=id,
    question = question,
    bank = bank,
//    topic = topic,
    explanation = explanation,
    hint = hint,
    marks = marks,
    bloomsTaxonomy = bloomsTaxonomy,
    co = co,
    negativeMark = negativeMark,
    difficulty = difficulty
){
    override fun copyQuestion(): DescriptiveQuestion {
        val copiedQuestion = DescriptiveQuestion(
            id = null,
            question = question,
            bank = bank,
//            topic = topic,
            explanation = explanation,
            hint = hint,
            marks = marks,
            bloomsTaxonomy = bloomsTaxonomy,
            co = co,
            negativeMark = negativeMark,
            difficulty = difficulty,
            expectedAnswer = expectedAnswer,
            strictness = strictness,
            guidelines = guidelines,

        )
        return copiedQuestion
    }
}