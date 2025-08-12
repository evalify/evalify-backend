package com.evalify.evalifybackend.quiz.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class QuizCleanupService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun clearCorruptedResponsesOnly(quizId: UUID, studentId: String?) {
        try {
            // Only clear the responses JSONB field - this is what's causing the corruption
            val sql = """
                UPDATE quiz_student 
                SET responses = '[]'::jsonb
                WHERE quiz_id = ? AND student_id = ?
            """.trimIndent()
            
            val query = entityManager.createNativeQuery(sql)
            query.setParameter(1, quizId)
            query.setParameter(2, studentId)
            val rowsUpdated = query.executeUpdate()
            
            println("QuizCleanupService: Cleared corrupted responses for quiz $quizId, student $studentId. Rows updated: $rowsUpdated")
        } catch (e: Exception) {
            println("QuizCleanupService: Error clearing responses: ${e.message}")
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) 
    fun deleteQuizStudentRecord(quizId: UUID, studentId: String?) {
        try {
            val deleteSql = "DELETE FROM quiz_student WHERE quiz_id = ? AND student_id = ?"
            val deleteQuery = entityManager.createNativeQuery(deleteSql)
            deleteQuery.setParameter(1, quizId)
            deleteQuery.setParameter(2, studentId)
            val rowsDeleted = deleteQuery.executeUpdate()
            println("QuizCleanupService: Deleted quiz student record. Rows deleted: $rowsDeleted")
        } catch (e: Exception) {
            println("QuizCleanupService: Error deleting record: ${e.message}")
        }
    }
}
