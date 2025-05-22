package com.evalify.evalifybackend.questions.domain

import com.evalify.evalifybackend.answer.QuestionAnswer
import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.topic.domain.Topic
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.Type
import java.util.UUID

@Entity
@DiscriminatorValue("MCQ")

class MCQ(
    override val id: UUID? = null,
     question: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    val bank: Bank,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val topic: MutableList<Topic> = mutableListOf(),

    var explanation: String? = "",
    var hint: String? = "",
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
) : BaseQuestion(id = id )