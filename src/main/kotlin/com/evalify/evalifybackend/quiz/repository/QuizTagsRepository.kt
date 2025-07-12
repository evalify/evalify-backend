package com.evalify.evalifybackend.quiz.repository

import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizTags
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.stereotype.Repository
import java.util.UUID


@Repository
interface QuizTagsRepository : JpaRepository<QuizTags, UUID>{
}