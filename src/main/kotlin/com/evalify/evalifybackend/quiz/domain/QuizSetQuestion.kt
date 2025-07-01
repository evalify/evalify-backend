package com.evalify.evalifybackend.quiz.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import jakarta.persistence.JoinColumn

@Entity
@Table(name = "quiz_set_question")
class QuizSetQuestion(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_set_id") // ✅ Must match `mappedBy = "quizSet"` in QuizSet
    val quizSet: QuizSet,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id") // ✅ Explicit join column
    val question: BaseQuestion,

    val order: Int // ✅ Include this if you want to preserve order
)

