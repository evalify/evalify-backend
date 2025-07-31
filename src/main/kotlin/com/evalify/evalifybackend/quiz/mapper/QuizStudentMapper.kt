package com.evalify.evalifybackend.quiz.mapper

import com.evalify.evalifybackend.quiz.domain.DTO.crud.QuizStudentDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizLabDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizStudentsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizStudentsManagerDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.StudentStatus
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.user.domain.User
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class QuizStudentMapper(
    private val quizStudentRepository: QuizStudentRepository,
) {

    fun mapQuizStudent(quiz: Quiz,students:List<QuizStudentsDTO>): QuizStudentsManagerDTO {
        return QuizStudentsManagerDTO(
            id=quiz.id,
            name=quiz.name,
            description=quiz.description,
            students = students,
            createdAt = quiz.createdAt,
            lab = quiz.lab.map { QuizLabDTO(it.id,it.name) },
        )
    }

    fun mapStudentToDto(student: User,quiz: Quiz): QuizStudentsDTO {
        val isActiveInQuiz = isInQuizStudent(student.id,quiz.id)
        val isCompletedQuiz = Instant.now().isAfter(quiz.endTime)
        return QuizStudentsDTO(
            id = student.id,
            name = student.email,
            email = student.email,
            phoneNumber = student.phoneNumber,
            status = if (isCompletedQuiz) {
                if (isActiveInQuiz) StudentStatus.COMPLETED else StudentStatus.MISSED
            } else {
                if (isActiveInQuiz) StudentStatus.ACTIVE else StudentStatus.INACTIVE
            }
        )
    }
   private fun isInQuizStudent(user: String?,quizId: UUID?): Boolean {
        return quizStudentRepository.findByQuizIdAndStudentId(userId = user.toString(),quizId = quizId) != null
    }
}