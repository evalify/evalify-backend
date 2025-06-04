package com.evalify.evalifybackend.section.domain

import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "section")
class Section(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val name: String,
    val isActive: Boolean = true,

    @OneToMany(fetch = FetchType.LAZY)
    val quizQuestions: MutableList<QuizQuestion> = mutableListOf(),

    @ManyToMany(mappedBy = "section")
    val quiz: List<Quiz> = listOf()

) {

}