package com.evalify.evalifybackend.quiz.domain

import com.evalify.evalifybackend.questions.domain.BaseQuestion
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID
import jakarta.persistence.CascadeType
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion


@Entity
@Table(name = "quiz_set")
class QuizSet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    val setNumber: Int,  // 0, 1, ..., quiz.noOfSets-1

    @ManyToOne(fetch = FetchType.LAZY)
    val quiz: Quiz,

    @OneToMany(mappedBy = "quizSet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val questions: MutableList<QuizSetQuestion> = mutableListOf()

)