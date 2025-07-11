package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.CodingBankReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.FunctionParamDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding.TestCaseType
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.CodingReturnDTO
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID
import org.hibernate.annotations.Type

@Entity
@DiscriminatorValue(value = "CODING")
class CodingQuestion(
        id: UUID?,
        question: String = "",
        bank: Bank?,
        topic: MutableList<Topic>,
        explanation: String? = "",
        hint: String? = "",
        marks: Int,
        bloomsTaxonomy: Taxonomy,
        co: Int,
        negativeMark: Int? = null,
        difficulty: Difficulty,
        val driverCode: String? = null,
        val boilerCode: String? = null,
        val functionName: String? = null,
        val returnType: String? = null,
        @Type(JsonBinaryType::class)
        @Column(columnDefinition = "jsonb")
        val params: List<FunctionParamDTO>? = emptyList(),
        @Type(JsonBinaryType::class)
        @Column(columnDefinition = "jsonb")
        val testcases: List<TestCaseDTO>,
        val language: List<String>? = emptyList(),
        val answer: String?
) :
        BaseQuestion(
                id = id,
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
        ) {
        override fun copyQuestion(): CodingQuestion {
                val copiedQuestion =
                        CodingQuestion(
                                id = null,
                                question = question,
                                bank = bank,
                                topic = topic,
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

        override fun mapToType(shuffleOptions: Boolean): QuestionsReturnDTO {
                return CodingReturnDTO(
                        question = this.question,
                        functionName = this.functionName,
                        returnType = this.returnType,
                        params = this.params,
                        language = this.language,
                        hint = this.hint,
                        marks = this.marks,
                        bloomsTaxonomy = this.bloomsTaxonomy,
                        driverCode = this.driverCode,
                        co = this.co,
                        difficulty = this.difficulty,
                        testcases = this.testcases.filter { testcase ->
                                testcase.tags == TestCaseType.SAMPLE
                        })

        }



        override fun mapToBankType(): BankQuestionsReturnDTO {
                return CodingBankReturnDTO(
                        question = this.question,
                        functionName = this.functionName,
                        returnType = this.returnType,
                        params = this.params,
                        language = this.language,
                        hint = this.hint,
                        marks = this.marks,
                        bloomsTaxonomy = this.bloomsTaxonomy,
                        co = this.co,
                        difficulty = this.difficulty,
                        driverCode = this.driverCode,
                        boilerCode = this.boilerCode,
                        testcases = this.testcases,
                        answer = this.answer,
                        type = this.getQuestionType()
                )
        }

        override fun patchWith(dto: PatchQuestionDTO): BaseQuestion? {
                val patchedQuestion =
                        CodingQuestion(
                                id = this.id,
                                question = dto.question ?: this.question,
                                bank = this.bank,
                                topic = dto.topic ?: this.topic,
                                explanation = dto.explanation ?: this.explanation,
                                hint = dto.hint ?: this.hint,
                                marks = dto.marks ?: this.marks,
                                bloomsTaxonomy = dto.bloomsTaxonomy ?: this.bloomsTaxonomy,
                                co = dto.co ?: this.co,
                                negativeMark = dto.negativeMark ?: this.negativeMark,
                                difficulty = dto.difficulty ?: this.difficulty,
                                driverCode = dto.driverCode ?: this.driverCode,
                                boilerCode = dto.boilerCode ?: this.boilerCode,
                                functionName = dto.functionName ?: this.functionName,
                                returnType = dto.returnType ?: this.returnType,
                                params = dto.params ?: this.params,
                                testcases = dto.testcases ?: this.testcases,
                                language = dto.language ?: this.language,
                                answer = dto.answer ?: this.answer
                        )
                return patchedQuestion
        }

        override fun getQuestionType(): QuestionTypes {
                return QuestionTypes.CODING
        }
}

class FunctionParam(val param: String, val type: String)

class TestCase(val input: List<Any>, val expected: Any)
