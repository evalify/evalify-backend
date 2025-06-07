package com.evalify.evalifybackend.quiz.question.repository

import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "quizquestion")
interface QuizQuestionRepository : JpaRepository<QuizQuestion, UUID> {
}