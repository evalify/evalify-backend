package com.evalify.evalifybackend.quiz.question.domain.MCQ

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.MCQBankReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MCQOptionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.McqReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID
import org.hibernate.annotations.Type

@Entity
@DiscriminatorValue(value = "MCQ")
class MCQ(
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
        val options: MutableList<MCQOption>
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
    override fun copyQuestion(): MCQ {
        val copiedQuestion =
                MCQ(
                        id = null,
                        question = question,
                        bank = bank,
                        topic = topic.toMutableList(),
                        explanation = explanation,
                        hint = hint,
                        marks = marks,
                        bloomsTaxonomy = bloomsTaxonomy,
                        co = co,
                        negativeMark = negativeMark,
                        difficulty = difficulty,
                        options = options.map { 
                            MCQOption(
                                id = null,
                                text = it.text,
                                isCorrect = it.isCorrect
                            )
                        }.toMutableList()
                )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions: Boolean): QuestionsReturnDTO {
        return McqReturnDTO(
                question = this.question,
                options =
                        if (shuffleOptions) options.shuffled().map { MCQOptionDTO(it.id, it.text) }
                        else options.map { MCQOptionDTO(it.id, it.text) },
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

    override fun mapToBankType(questionId:UUID?): BankQuestionsReturnDTO {
        return MCQBankReturnDTO(
                question = this.question,
                options = this.options,
                hint = this.hint,
                marks = this.marks,
                bloomsTaxonomy = this.bloomsTaxonomy,
                co = this.co,
                difficulty = this.difficulty,
                explanation = this.explanation,
                type = this.getQuestionType(),
            questionId = questionId,
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
        return QuestionTypes.MCQ
    }

    override fun patchWith(dto: PatchQuestionDTO): BaseQuestion? {

        val patchedQuestion =
                MCQ(
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
                        options = dto.options ?: this.options
                )
        return patchedQuestion
    }
}
