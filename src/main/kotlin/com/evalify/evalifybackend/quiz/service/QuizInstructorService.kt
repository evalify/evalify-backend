package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizPreviewDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizUpdateDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.mapper.updateQuiz
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuizInstructorService(val quizRepository: QuizRepository) {

    /**
     * Retrieves a list of quizzes for a specific course
     * @param courseId The UUID of the course
     * @return List of QuizPreviewDTO objects
     */
    fun getQuizzesByCourseId(courseId: UUID): List<QuizPreviewDTO> {
        val quizzes = quizRepository.findByCourseId(courseId)

        return quizzes.map { quiz ->
            mapToQuizPreviewDTO(quiz)
        }
    }

    /**
     * Maps a Quiz entity to a QuizPreviewDTO
     * @param quiz The Quiz entity to map
     * @return QuizPreviewDTO object
     */
    private fun mapToQuizPreviewDTO(quiz: Quiz): QuizPreviewDTO {
        return QuizPreviewDTO(
            id = quiz.id,
            name = quiz.name,
            description = quiz.description ?: "",
            startTime = quiz.startTime,
            endTime = quiz.endTime,
            batches = quiz.batch.map { it.name },
            labs = quiz.lab.map { it.name },
            duration = quiz.duration,
            publishResult = quiz.publishResult
        )
    }

    /**
     * Updates a quiz with the provided data
     * @param quizId The UUID of the quiz to update
     * @param quizUpdateDTO The DTO containing the updated quiz data
     * @return The updated Quiz entity
     * @throws NotFoundException if the quiz is not found
     */
    fun updateQuiz(quizId: UUID, quizUpdateDTO: QuizUpdateDTO): Quiz {
        val quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }

        val updatedQuiz = quiz.updateQuiz(quizUpdateDTO)
        return quizRepository.save(updatedQuiz)
    }

    fun getAllQuizzesForInstructor(): List<QuizPreviewDTO> {
        val quizzes = quizRepository.findAll()
        return quizzes.map { quiz ->
            mapToQuizPreviewDTO(quiz)
        }
    }
}
