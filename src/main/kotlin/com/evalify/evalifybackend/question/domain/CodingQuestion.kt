package com.evalify.evalifybackend.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.topic.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type

@Entity
@DiscriminatorValue(value = "CODING")
class CodingQuestion(
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
    val driverCode: String,
    val boilerCode: String,
    val functionName: String,
    val returnType: String,
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val params: List<FunctionParam>,
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val testcases: List<TestCase>,
    val language: List<String>,
    val answer: String
): BaseQuestion(
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
)

 class FunctionParam(val param: String, val type: String)

 class TestCase(val input: List<Any>, val expected: Any)
