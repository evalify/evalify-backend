package com.evalify.evalifybackend.question.repository

import com.evalify.evalifybackend.question.domain.quizQuestion.QuizQuestion
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface QuizQuestionRepository : JpaRepository<QuizQuestion, UUID> {
}