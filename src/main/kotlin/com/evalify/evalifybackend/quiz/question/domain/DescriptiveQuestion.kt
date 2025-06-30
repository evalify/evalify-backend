package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.DescriptiveBankReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.DescriptiveReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID


@Entity
@DiscriminatorValue(value = "DESCRIPTIVE")
class DescriptiveQuestion(
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
    val expectedAnswer:String?,
    val strictness:Float?,
    val guidelines:String?,
    val answer : String?
) : BaseQuestion(
    id=id,
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
    override fun copyQuestion(): DescriptiveQuestion {
        val copiedQuestion = DescriptiveQuestion(
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
            expectedAnswer = expectedAnswer,
            strictness = strictness,
            guidelines = guidelines,
            answer = answer

        )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions:Boolean): QuestionsReturnDTO {
        return DescriptiveReturnDTO(
            question = this.question,
            hintText = this.hint,
            markValue = this.marks,
            taxonomy = this.bloomsTaxonomy,
            coValue = this.co,
            difficultyLevel = this.difficulty
        )
    }

    override fun mapToBankType(): BankQuestionsReturnDTO {
        return DescriptiveBankReturnDTO(
            question = this.question,
            hintText = this.hint,
            markValue = this.marks,
            taxonomy = this.bloomsTaxonomy,
            coValue = this.co,
            difficultyLevel = this.difficulty,
            expectedAnswer = this.expectedAnswer,
            strictness = this.strictness,
            guidelines = this.guidelines,
            explanation = this.explanation,
            answer = this.answer,
            type = this.getQuestionType()

        )
    }
    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.DESCRIPTIVE
    }

    override fun patchWith(dto: PatchQuestionDTO): BaseQuestion? {
        val patchedQuestion = DescriptiveQuestion(
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
            expectedAnswer = dto.expectedAnswer ?: this.expectedAnswer,
            strictness = dto.strictness ?: this.strictness,
            guidelines = dto.guidelines ?: this.guidelines,
            answer = dto.answer ?: this.answer
        )

        return patchedQuestion
    }
}