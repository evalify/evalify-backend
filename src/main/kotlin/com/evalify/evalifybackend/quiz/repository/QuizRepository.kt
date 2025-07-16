package com.evalify.evalifybackend.quiz.repository

import com.evalify.evalifybackend.quiz.domain.Quiz
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.time.Instant
import java.util.Date
import java.util.UUID

interface QuizRepository: JpaRepository<Quiz, UUID> {
    @Query("SELECT q FROM Quiz q JOIN q.course c WHERE c.id = :courseId")
    fun findByCourseId(@Param("courseId") courseId: UUID): List<Quiz>

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.startTime < :startTime AND q.endTime > :endTime")
    fun countByStartTimeBeforeAndEndTimeAfter(
        @Param("startTime") startTime: Instant,
        @Param("endTime") endTime: Instant
    ): Long

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.startTime > :date")
    fun countByStartTimeAfter(@Param("date") date: Instant): Long

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.endTime < :date")
    fun countByEndTimeBefore(@Param("date") date: Instant): Long

    @Query("""
        SELECT q FROM Quiz q 
        JOIN q.sharedUsers qu
        WHERE qu.user.id = :userId
    """)
    fun findByUserId(@Param("userId") userId: String): List<Quiz>

    @Query("""
        SELECT q FROM Quiz q 
        JOIN q.student s
        WHERE s.id = :studentId
    """)
    fun findByStudentId(@Param("studentId") studentId: String): List<Quiz>
}
