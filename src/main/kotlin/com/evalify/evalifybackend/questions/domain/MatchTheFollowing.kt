package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.MatchBankReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.questionTypes.PairDTO
import com.evalify.evalifybackend.bank.domain.DTO.topic.ReturnTopicDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MatchReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.MatchShuffleDTO
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.evalify.evalifybackend.quiz.domain.DTO.questionTypes.Pair
import com.evalify.evalifybackend.topic.repository.TopicRepo
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID
import org.hibernate.annotations.Type

class MatchPair( val leftPair: UUID, val rightPair: List<UUID>)



@Entity
@DiscriminatorValue(value = "MATCH_THE_FOLLOWING")
class MatchTheFollowing(
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
        val keys: MutableList<PairDTO>? = mutableListOf(),

    @Type(JsonBinaryType::class)
        @Column(columnDefinition = "jsonb")
        val values: MutableList<PairDTO>? = mutableListOf(),

    @Type(JsonBinaryType::class)
        @Column(columnDefinition = "json")
        val matchPair : MutableList<MatchPair>? = mutableListOf()
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
    override fun copyQuestion(): MatchTheFollowing {
        val copiedQuestion =
                MatchTheFollowing(
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
                        keys = keys,
                        values = values,
                        matchPair = matchPair
                )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions: Boolean): QuestionsReturnDTO {
        val left = keys?.shuffled()?.toMutableList()
        val right = values?.shuffled()?.toMutableList()


        // zipping the shuffled lists together to form a list of pairs
        val pairs = MatchShuffleDTO(left = left,right = right)

        return MatchReturnDTO(
                question = this.question,
                keyValues = pairs,
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
        return MatchBankReturnDTO(
                question = this.question,
                questionId = questionId,
                hint = this.hint,
                keyValues = MatchShuffleDTO(left = this.keys , right = this.values),
                marks = this.marks,
                bloomsTaxonomy = this.bloomsTaxonomy,
                co = this.co,
                difficulty = this.difficulty,
                explanation = this.explanation,
                type = this.getQuestionType(),

            topics = this.topic.map {
                    topic ->
                ReturnTopicDTO(
                    topic.id,
                    topic.name
                )
            },
            matchPair = this.matchPair

        )
    }

    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.MATCH_THE_FOLLOWING
    }

    override fun patchWith(dto: PatchQuestionDTO,topicRepo: TopicRepo): BaseQuestion? {
        val finalTopics = if (!dto.topic.isNullOrEmpty()) {
            topicRepo.findAllById(dto.topic)
        } else {
            null
        }
        val patchedQuestion =
                MatchTheFollowing(
                        id = this.id,
                        question = dto.question ?: this.question,
                        bank = this.bank,
                        topic = finalTopics ?: this.topic,
                        explanation = dto.explanation ?: this.explanation,
                        hint = dto.hint ?: this.hint,
                        marks = dto.marks ?: this.marks,
                        bloomsTaxonomy = dto.bloomsTaxonomy ?: this.bloomsTaxonomy,
                        co = dto.co ?: this.co,
                        difficulty = dto.difficulty ?: this.difficulty,
                        negativeMark = dto.negativeMark ?: this.negativeMark,
                        keys = dto.keys ?: this.keys,
                        values = dto.values ?: this.values,
                        matchPair = dto.matchPair ?: this.matchPair
                )
        return patchedQuestion
    }
}
