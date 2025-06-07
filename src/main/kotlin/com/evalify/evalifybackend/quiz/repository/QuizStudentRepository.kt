package com.evalify.evalifybackend.quiz.repository

import com.evalify.evalifybackend.quiz.domain.QuizStudent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface QuizStudentRepository : JpaRepository<QuizStudent, UUID>{
    fun findByQuizIdAndStudentId( quizId: UUID, userId: UUID): QuizStudent?
}