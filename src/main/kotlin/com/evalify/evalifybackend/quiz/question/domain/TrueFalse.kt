package com.evalify.evalifybackend.quiz.question.domain.MCQ

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.MCQOptionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.McqReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.TrueFalseDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type
import java.util.UUID

@Entity
@DiscriminatorValue(value = "TRUE_FALSE")
class TrueFalse(
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
    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val answer : Boolean,
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
    override fun copyQuestion(): TrueFalse {
        val copiedQuestion = TrueFalse(
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
            answer = answer,
        )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions:Boolean): QuestionsReturnDTO {
        return TrueFalseDTO(
            question = this.question,
            answers = this.answer,
            hintText = this.hint,
            markValue = this.marks,
            taxonomy = this.bloomsTaxonomy,
            coValue = this.co,
            difficultyLevel = this.difficulty
        )
    }

    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.TRUEFALSE
    }

    override fun patchWith(dto: com.evalify.evalifybackend.bank.domain.DTO.PatchQuestionDTO): BaseQuestion? {
        val patchedQuestion = TrueFalse(
            id = this.id,
            question = dto.question ?: this.question,
            bank = this.bank,
            topic = dto.topic ?: this.topic,
            explanation = dto.explanation ?: this.explanation,
            hint = dto.hint ?: this.hint,
            marks = dto.marks ?: this.marks,
            bloomsTaxonomy = dto.bloomsTaxonomy ?: this.bloomsTaxonomy,
            co = dto.co ?: this.co,
            difficulty = dto.difficulty ?: this.difficulty,
            answer = dto.trueFalseAnswer ?: this.answer
        )
        return patchedQuestion
    }
}