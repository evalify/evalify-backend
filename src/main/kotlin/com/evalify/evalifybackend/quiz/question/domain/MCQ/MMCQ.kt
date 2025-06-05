package com.evalify.evalifybackend.quiz.question.domain.MCQ

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.MCQOptionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.McqReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type
import java.util.UUID

@Entity
@DiscriminatorValue(value = "MMCQ")
class MMCQ(
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
    val options: MutableList<MCQOption> = mutableListOf<MCQOption>()
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
    override fun copyQuestion(): MMCQ {
        val copiedQuestion = MMCQ(
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
            options = options
        )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions: Boolean ): QuestionsReturnDTO {
        return McqReturnDTO(
            question = this.question,
            options = if (shuffleOptions) options.shuffled().map { MCQOptionDTO(it.id, it.text) }
            else options.map { MCQOptionDTO(it.id, it.text) },
            hintText = this.hint,
            markValue = this.marks,
            taxonomy = this.bloomsTaxonomy,
            coValue = this.co,
            difficultyLevel = this.difficulty
        )
    }

    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.MMCQ
    }
}