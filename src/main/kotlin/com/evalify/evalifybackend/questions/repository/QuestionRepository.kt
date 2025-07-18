package com.evalify.evalifybackend.quiz.question.repository

import com.evalify.evalifybackend.questions.domain.BaseQuestion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "question")
interface QuestionRepository : JpaRepository<BaseQuestion, UUID>{

}