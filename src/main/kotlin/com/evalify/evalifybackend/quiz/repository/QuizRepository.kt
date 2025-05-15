package com.evalify.evalifybackend.quiz.repository

import com.evalify.evalifybackend.quiz.domain.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "quiz")
interface QuizRepository: JpaRepository<Quiz, UUID> {
}