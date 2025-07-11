package com.evalify.evalifybackend.quiz.question.domain.MCQ

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.TrueFalseBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.TrueFalseDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID
import org.hibernate.annotations.Type

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
        @Type(JsonBinaryType::class) @Column(columnDefinition = "jsonb") val answer: Boolean,
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
    override fun copyQuestion(): TrueFalse {
        val copiedQuestion =
                TrueFalse(
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

    override fun mapToType(shuffleOptions: Boolean): QuestionsReturnDTO {
        return TrueFalseDTO(
                question = this.question,
                hint = this.hint,
                marks = this.marks,
                bloomsTaxonomy = this.bloomsTaxonomy,
                co = this.co,
                difficulty = this.difficulty,
            questionId = this.id,
            topics = this.topic.map {
                    topic ->
                ReturnTopicDTO(
                    topic.id,
                    topic.name
                )
            }
        )
    }

    override fun mapToBankType(): BankQuestionsReturnDTO {
        return TrueFalseBankDTO(
                question = this.question,
                answers = this.answer,
                hint = this.hint,
                marks = this.marks,
                bloomsTaxonomy = this.bloomsTaxonomy,
                co = this.co,
                difficulty = this.difficulty,
                explanation = this.explanation,
                type = this.getQuestionType(),
            questionId = this.id,
            topics = this.topic.map {
                    topic ->
                ReturnTopicDTO(
                    topic.id,
                    topic.name
                )
            }
        )
    }

    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.TRUEFALSE
    }

    override fun patchWith(dto: PatchQuestionDTO): BaseQuestion? {
        val patchedQuestion =
                TrueFalse(
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
