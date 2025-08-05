package com.evalify.evalifybackend.quiz.repository


import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizSet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuizSetRepository : JpaRepository<QuizSet, UUID> {
    fun findByQuiz(quiz: Quiz) : List<QuizSet>
    
    fun findByQuiz_Id(quizId: UUID) : List<QuizSet>

    fun findBySetNumber(setNumber: Int) : QuizSet?
}
