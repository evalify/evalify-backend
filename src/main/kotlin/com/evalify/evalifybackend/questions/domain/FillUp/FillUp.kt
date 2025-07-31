package com.evalify.evalifybackend.quiz.question.domain.FillUp

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.FillUpsBankReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.BankBlanksDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.BlankType
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.BlanksDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.FillUpsReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.checkerframework.common.aliasing.qual.Unique
import java.util.UUID
import org.hibernate.annotations.Type
import java.util.UUID.randomUUID

class blanks(

        val id: Int,
        val answers: List<String>,
        val type: BlankType)

@Entity
@DiscriminatorValue(value = "FILL_UP")
class FillUp(
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
        val strictMatch: Boolean?,
        val llmEval: Boolean?,
        @Type(JsonBinaryType::class) @Column(columnDefinition = "jsonb") val blanks: List<blanks>
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
    override fun copyQuestion(): FillUp {
        val copiedQuestion =
                FillUp(
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
                        strictMatch = strictMatch,
                        llmEval = llmEval,
                        blanks = blanks.map {
                            blanks(
                                id = it.id,
                                answers = it.answers.toList(),
                                    type = it.type
                            )
                        }
                )
        return copiedQuestion
    }

        override fun mapToType(shuffleOptions: Boolean): QuestionsReturnDTO {
                return FillUpsReturnDTO(
                        question = this.question,
                        blankIds = this.blanks.mapIndexed { index, blanks ->
                                BlanksDTO(id = blanks.id, sNo = index + 1, type = blanks.type)
                        },
                        hint = this.hint,
                        marks = this.marks,
                        bloomsTaxonomy = this.bloomsTaxonomy,
                        co = this.co,
                        difficulty = this.difficulty,
                        questionId = this.id,
                        topics = this.topic.map { topic ->
                                ReturnTopicDTO(topic.id, topic.name)
                        }
                )
        }


        override fun mapToBankType(questionId: UUID?): BankQuestionsReturnDTO {
                return FillUpsBankReturnDTO(
                        question = this.question,
                        blanks = this.blanks.mapIndexed { index, blanks ->
                                BankBlanksDTO(
                                        id = blanks.id,
                                        sNo = index + 1,
                                        type = blanks.type,
                                        answers = blanks.answers.toList()
                                )
                        },
                        hint = this.hint,
                        marks = this.marks,
                        bloomsTaxonomy = this.bloomsTaxonomy,
                        co = this.co,
                        difficulty = this.difficulty,
                        strictMatch = this.strictMatch,
                        llmEval = this.llmEval,
                        explanation = this.explanation,
                        type = this.getQuestionType(),
                        questionId = questionId,
                        topics = this.topic.map { topic ->
                                ReturnTopicDTO(topic.id, topic.name)
                        }
                )
        }

        override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.FILL_UP
    }

    override fun patchWith(dto: PatchQuestionDTO,topicRepo: TopicRepo): BaseQuestion? {
            val finalTopics = if (!dto.topic.isNullOrEmpty()) {
                    topicRepo.findAllById(dto.topic)
            } else {
                    null
            }
        val patchedQuestion =
                FillUp(
                        id = this.id,
                        bank = this.bank,
                        topic = finalTopics?: this.topic,
                        question = dto.question ?: this.question,
                        explanation = dto.explanation ?: this.explanation,
                        hint = dto.hint ?: this.hint,
                        marks = dto.marks ?: this.marks,
                        bloomsTaxonomy = dto.bloomsTaxonomy ?: this.bloomsTaxonomy,
                        co = dto.co ?: this.co,
                        negativeMark = dto.negativeMark ?: this.negativeMark,
                        difficulty = dto.difficulty ?: this.difficulty,
                        strictMatch = dto.strictMatch ?: this.strictMatch,
                        llmEval = dto.llmEval ?: this.llmEval,
                        blanks = dto.blanks ?: this.blanks
                )
        return patchedQuestion
    }
}
