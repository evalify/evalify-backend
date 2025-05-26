package com.evalify.evalifybackend.question.domain.MCQ

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.topic.domain.Topic
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
@DiscriminatorValue(value = "MCQ")
class MCQ(
    question: String = "",
    bank: Bank,
    topic: MutableList<Topic>,
    explanation: String? = "",
    hint: String? = "",
    type: QuestionTypes,
    marks: Int,
    bloomsTaxonomy: Taxonomy,
    co: Int,
    negativeMark: Int? = null,
    difficulty: Difficulty,
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val options: MutableList<MCQOption> = mutableListOf<MCQOption>()
) : BaseQuestion(
    question = question,
    bank = bank,
    topic = topic,
    explanation = explanation,
    hint = hint,
    type = type,
    marks = marks,
    bloomsTaxonomy = bloomsTaxonomy,
    co = co,
    negativeMark = negativeMark,
    difficulty = difficulty
){
}