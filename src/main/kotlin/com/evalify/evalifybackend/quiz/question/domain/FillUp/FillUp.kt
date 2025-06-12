package com.evalify.evalifybackend.quiz.question.domain.FillUp

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.BlankDTO
import com.evalify.evalifybackend.bank.domain.DTO.PatchQuestionDTO
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.BlanksDTO
import com.evalify.evalifybackend.quiz.domain.DTO.FillUpsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import org.hibernate.annotations.Type
import java.util.UUID


class blanks( val id : String, val answers : List<String>)

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
    val template: String?,

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val blanks: List<blanks>
) : BaseQuestion(
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
){
    override fun copyQuestion(): FillUp {
        val copiedQuestion = FillUp(
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
            strictMatch = strictMatch,
            llmEval = llmEval,
            template = template,
            blanks = blanks
        )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions:Boolean): QuestionsReturnDTO {
        return FillUpsReturnDTO(
            question = this.question,
            blankIds = this.blanks.map{blanks -> BlanksDTO(id = blanks.id)},
            hintText = this.hint,
            markValue = this.marks,
            taxonomy = this.bloomsTaxonomy,
            coValue = this.co,
            difficultyLevel = this.difficulty
        )
    }
    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.FILL_UP
    }

    override fun patchWith(dto: PatchQuestionDTO): BaseQuestion? {
        val patchedQuestion = FillUp(
                id = this.id,
                bank = this.bank,
                topic = dto.topic?:this.topic,
                question = dto.question?:this.question,
                explanation = dto.explanation?:this.explanation,
                hint = dto.hint?:this.hint,
                marks = dto.marks?:this.marks,
                bloomsTaxonomy = dto.bloomsTaxonomy?:this.bloomsTaxonomy,
                co = dto.co?:this.co,
                negativeMark = dto.negativeMark?:this.negativeMark,
                difficulty = dto.difficulty?:this.difficulty,
                strictMatch = dto.strictMatch?:this.strictMatch,
                llmEval = dto.llmEval?:this.llmEval,
                template = dto.template?:this.template,
                blanks = dto.blanks?:this.blanks

        )
        return patchedQuestion }
}