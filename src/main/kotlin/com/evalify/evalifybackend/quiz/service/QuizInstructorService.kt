package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizPreviewDTO
import com.evalify.evalifybackend.quiz.domain.DTO.quiz.QuizUpdateDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizStatus
import com.evalify.evalifybackend.quiz.mapper.updateQuiz
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.Instant.now
import java.util.UUID

@Service
@Transactional
class QuizInstructorService(val quizRepository: QuizRepository) {

    /**
     * Retrieves a list of quizzes for a specific course
     * @param courseId The UUID of the course
     * @return List of QuizPreviewDTO objects
     */
    fun getQuizzesByCourseId(courseId: String,status: QuizStatus?,userId:String): List<QuizPreviewDTO> {
        if(courseId == "all") return getAllQuizzesForInstructor(userId)

        val courseId = UUID.fromString(courseId)
        val quizzes = quizRepository.findByCourseId(courseId)
        if(status != null){
            return quizzes.filter { it.status == status }.map { quiz ->
                mapToQuizPreviewDTO(quiz)
            }
        }


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
        // Initialize lazy collections within the transaction boundary
        val batchNames = try {
            quiz.batch.map { it.name }
        } catch (e: Exception) {
            emptyList<String>()
        }
        
        val labNames = try {
            quiz.lab.map { it.name }
        } catch (e: Exception) {
            emptyList<String>()
        }
        val isProtected = !quiz.password.isNullOrEmpty()
        val status: QuizStatus = when{
            quiz.startTime.isBefore(Instant.now()) -> QuizStatus.UPCOMING
            quiz.endTime.isAfter(Instant.now()) -> QuizStatus.COMPLETED
            else -> QuizStatus.ACTIVE
        }
        
        return QuizPreviewDTO(
            id = quiz.id,
            name = quiz.name,
            description = quiz.description ?: "",
            startTime = quiz.startTime,
            endTime = quiz.endTime,
            batches = batchNames,
            labs = labNames,
            duration = quiz.duration,
            publishResult = quiz.publishResult,
            status = status,
            isProtected = isProtected,
            courseCodes = quiz.course.map { it.code }
        )
    }

    /**
     * Updates a quiz with the provided data
     * @param quizId The UUID of the quiz to update
     * @param quizUpdateDTO The DTO containing the updated quiz data
     * @return The updated Quiz preview DTO
     * @throws NotFoundException if the quiz is not found
     */
    fun updateQuiz(quizId: UUID, quizUpdateDTO: QuizUpdateDTO): QuizPreviewDTO {
        val quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }

        val updatedQuiz = quiz.updateQuiz(quizUpdateDTO)
        val savedQuiz = quizRepository.save(updatedQuiz)
        
        // Return DTO instead of entity to avoid lazy loading issues
        return mapToQuizPreviewDTO(savedQuiz)
    }

    fun getAllQuizzesForInstructor(userId: String): List<QuizPreviewDTO> {
        val quizzes = quizRepository.findByUserId(userId)
        return quizzes.map { quiz ->
            mapToQuizPreviewDTO(quiz)
        }
    }
}
