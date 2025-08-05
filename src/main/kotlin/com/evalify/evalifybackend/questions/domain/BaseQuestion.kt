package com.evalify.evalifybackend.questions.domain

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.bank.BankQuestionsReturnDTO
import com.evalify.evalifybackend.bank.domain.DTO.crud.PatchQuestionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuestionsReturnDTO

import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import java.util.UUID
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.topic.repository.TopicRepo
import jakarta.persistence.Column


enum class Taxonomy {
    REMEMBER,
    UNDERSTAND,
    APPLY,
    ANALYSE,
    EVALUATE,
    CREATE
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,

}

enum class QuestionTypes{
    MCQ,
    MMCQ,
    TRUEFALSE,
    FILL_UP,
    MATCH_THE_FOLLOWING,
    DESCRIPTIVE,
    FILE_UPLOAD,
    CODING
}
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Question_Type" , discriminatorType = DiscriminatorType.STRING)
@Table(name = "question")
abstract class BaseQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID? = null,

    @Column(columnDefinition = "TEXT")
    val question: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    val bank: Bank? = null,

    @ManyToMany(fetch = FetchType.LAZY)
    val topic: MutableList<Topic> = mutableListOf(),

    @Column(columnDefinition = "TEXT")
    val explanation: String? = "",

    @Column(columnDefinition = "TEXT")
    val hint: String? = "",
//    val type: QuestionTypes,
    val marks: Int,
    @Enumerated(EnumType.STRING)
    val bloomsTaxonomy: Taxonomy,
    val co: Int,
    val negativeMark: Int? = null,
    @Enumerated(EnumType.STRING)
    val difficulty: Difficulty


//
//    @Type(JsonBinaryType::class)
//    @Column(columnDefinition = "jsonb")
//    var answer: QuestionAnswer
){
    abstract fun copyQuestion(): BaseQuestion

    abstract fun mapToType(shuffleOptions: Boolean = false): QuestionsReturnDTO

    abstract fun mapToBankType(questionId:UUID?) : BankQuestionsReturnDTO

    abstract fun getQuestionType(): QuestionTypes

    abstract fun patchWith(dto : PatchQuestionDTO, topicRepo: TopicRepo) : BaseQuestion?
}



