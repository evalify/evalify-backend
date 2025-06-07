package com.evalify.evalifybackend.quiz.question.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import com.evalify.evalifybackend.quiz.domain.DTO.DescriptiveReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.FileUploadDTO
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.util.UUID


@Entity
@DiscriminatorValue(value = "FILE_UPLOAD")
class FileUpload(
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
    val guidelines:String?
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
    override fun copyQuestion(): FileUpload{
        val copiedQuestion = FileUpload(
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

            )
        return copiedQuestion
    }

    override fun mapToType(shuffleOptions:Boolean): QuestionsReturnDTO {
        return FileUploadDTO(
            question = this.question,
            hintText = this.hint,
            markValue = this.marks,
            taxonomy = this.bloomsTaxonomy,
            coValue = this.co,
            difficultyLevel = this.difficulty
        )
    }
    override fun getQuestionType(): QuestionTypes {
        return QuestionTypes.FILE_UPLOAD
    }
}