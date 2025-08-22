package com.evalify.evalifybackend.quiz.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface QuizEvaluationRepository : JpaRepository<com.evalify.evalifybackend.quiz.domain.QuizStudent, Long> {

    /**
     * Check if a quiz student record exists for grading
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM quiz_student WHERE quiz_id = :quizId AND student_id = :studentId", nativeQuery = true)
    fun existsByQuizIdAndStudentId(@Param("quizId") quizId: UUID, @Param("studentId") studentId: String): Boolean

    /**
     * Get submission status for grading verification
     */
    @Query(value = "SELECT is_submitted FROM quiz_student WHERE quiz_id = :quizId AND student_id = :studentId", nativeQuery = true)
    fun getSubmissionStatus(@Param("quizId") quizId: UUID, @Param("studentId") studentId: String): Boolean?

    /**
     * Get responses as raw JSON string to avoid JPA serialization issues
     */
    @Query(value = "SELECT responses FROM quiz_student WHERE quiz_id = :quizId AND student_id = :studentId", nativeQuery = true)
    fun getResponsesAsJson(@Param("quizId") quizId: UUID, @Param("studentId") studentId: String): String?

    /**
     * Update responses with graded data using raw JSON to avoid serialization issues
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE quiz_student SET responses = CAST(:responsesJson AS jsonb) WHERE quiz_id = :quizId AND student_id = :studentId", nativeQuery = true)
    fun updateResponsesWithGrades(
        @Param("quizId") quizId: UUID, 
        @Param("studentId") studentId: String, 
        @Param("responsesJson") responsesJson: String
    ): Int

    /**
     * Get quiz student basic info without responses to avoid serialization issues
     */
    @Query(value = "SELECT id, quiz_id, student_id, is_submitted, submit_time FROM quiz_student WHERE quiz_id = :quizId AND student_id = :studentId", nativeQuery = true)
    fun getQuizStudentBasicInfo(@Param("quizId") quizId: UUID, @Param("studentId") studentId: String): Array<Any>?
}
