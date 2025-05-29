package com.evalify.evalifybackend.question.domain.FillUp

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.question.domain.Topic
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type

@Entity
@DiscriminatorValue(value = "FILL_UP")
class FillUp(
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
    val strictMatch: Boolean?,
    val llmEval: Boolean?,
    val template: String?,

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val blanks: List<List<String>>
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