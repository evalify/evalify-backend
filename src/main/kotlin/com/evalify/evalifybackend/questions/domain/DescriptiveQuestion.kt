package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.DescriptiveBankReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.DescriptiveReturnDTO
import com.evalify.evalifybackend.topic.repository.TopicRepo
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
        val expectedAnswer: String?,
        val strictness: Float?,
        val guidelines: String?,
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
    override fun copyQuestion(): DescriptiveQuestion {
        val copiedQuestion =
                DescriptiveQuestion(
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
                        expectedAnswer = expectedAnswer,
                        strictness = strictness,
                        guidelines = guidelines
                )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions: Boolean): QuestionsReturnDTO {
        return DescriptiveReturnDTO(
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

    override fun mapToBankType(questionId:UUID?): BankQuestionsReturnDTO {
        return DescriptiveBankReturnDTO(
                question = this.question,
                hint = this.hint,
                marks = this.marks,
                bloomsTaxonomy = this.bloomsTaxonomy,
                co = this.co,
                difficulty = this.difficulty,
                expectedAnswer = this.expectedAnswer,
                strictness = this.strictness,
                guidelines = this.guidelines,
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
        return QuestionTypes.DESCRIPTIVE
    }

    override fun patchWith(dto: PatchQuestionDTO,topicRepo: TopicRepo): BaseQuestion? {
            val finalTopics = if (!dto.topic.isNullOrEmpty()) {
                    topicRepo.findAllById(dto.topic)
            } else {
                    null
            }
        val patchedQuestion =
                DescriptiveQuestion(
                        id = this.id,
                        question = dto.question ?: this.question,
                        bank = this.bank,
                        topic = finalTopics ?: this.topic,
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
                )

        return patchedQuestion
    }
}
