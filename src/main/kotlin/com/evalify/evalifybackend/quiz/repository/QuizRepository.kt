package com.evalify.evalifybackend.quiz.repository

import com.evalify.evalifybackend.quiz.domain.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "quiz")
interface QuizRepository: JpaRepository<Quiz, UUID> {
    @Query("SELECT q FROM Quiz q JOIN q.course c WHERE c.id = :courseId")
    fun findByCourseId(@Param("courseId") courseId: UUID): List<Quiz>
}
