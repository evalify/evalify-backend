

import com.evalify.evalifybackend.answer.QuestionAnswer
import com.evalify.evalifybackend.topic.domain.Topic



import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.questions.domain.Taxonomy
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID
import jakarta.persistence.Column

@Entity
@Table(name = "quiz_question")
class QuizQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val question: String = "",

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val topic: MutableList<Topic> = mutableListOf(),

    val explanation: String? = "",
    val hint : String? = "",
    val type : QuestionTypes,
    val marks : Int,
    val bloomsTaxonomy : Taxonomy,
    val co : Int,
    val negativeMark : Int? = null,
    val difficulty : Difficulty,
    @ManyToOne(fetch = FetchType.LAZY)
    val bank : Bank,

    @Type(JsonBinaryType::class)
    @Column(columnDefinition = "jsonb")
    val answer: QuestionAnswer




)