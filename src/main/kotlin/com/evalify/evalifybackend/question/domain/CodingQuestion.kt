package com.evalify.evalifybackend.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type
import java.util.UUID

@Entity
@DiscriminatorValue(value = "CODING")
class CodingQuestion(
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
    val driverCode: String?,
    val boilerCode: String?,
    val functionName: String?,
    val returnType: String?,
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val params: List<FunctionParam>?,
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val testcases: List<TestCase>?,
    val language: List<String>?,
    val answer: String?
): BaseQuestion(
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
    override fun copyQuestion(): CodingQuestion {
        val copiedQuestion = CodingQuestion(
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
            driverCode = driverCode,
            boilerCode = boilerCode,
            functionName = functionName,
            returnType = returnType,
            params = params,
            testcases = testcases,
            language = language,
            answer = answer
        )
        return copiedQuestion
    }
}

 class FunctionParam(val param: String, val type: String)

 class TestCase(val input: List<Any>, val expected: Any)
