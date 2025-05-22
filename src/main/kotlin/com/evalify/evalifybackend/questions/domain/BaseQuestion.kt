package com.evalify.evalifybackend.questions.domain

import com.evalify.evalifybackend.answer.QuestionAnswer
import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.topic.domain.Topic

import org.hibernate.annotations.Type
import com.vladmihalcea.hibernate.type.json.JsonBinaryType

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.OneToMany
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToOne
import java.util.UUID





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
@Table(name = "base_question")
abstract class BaseQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val question: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    val bank: Bank,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val topic: MutableList<Topic> = mutableListOf(),

    val explanation: String? = "",
    val hint: String? = "",
    var type: QuestionTypes,
    var marks: Int,
    var bloomsTaxonomy: Taxonomy,
    var co: Int,
    var negativeMark: Int? = null,
    var difficulty: Difficulty,


    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    var answer: QuestionAnswer
)
//TODO() change this to a kotlin class rather than file ?




