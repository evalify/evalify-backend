package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizLabDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizStudentsDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.QuizStudentsManagerDTO
import com.evalify.evalifybackend.quiz.domain.DTO.manager.StudentStatus
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.exception.QuizNotFoundException
import com.evalify.evalifybackend.quiz.mapper.QuizStudentMapper
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
@Transactional
class QuizStudentsManagerService(
    val quizRepository: QuizRepository,
    val quizStudentMapper: QuizStudentMapper,
) {
    fun getQuizStudents(quizId: UUID):QuizStudentsManagerDTO{
        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow{ QuizNotFoundException(quizId.toString()) }

        val quizStudents: List<QuizStudentsDTO> = quiz.student.map { student-> quizStudentMapper.mapStudentToDto(student,quiz) }
        val courseStudents:List<QuizStudentsDTO> = quiz.course.flatMap { course -> course.students }.map { student->quizStudentMapper.mapStudentToDto(student,quiz) }
        val batchStudents:List<QuizStudentsDTO> = quiz.batch.flatMap { batch->batch.students }.map { student->quizStudentMapper.mapStudentToDto(student,quiz) }
        val students:List<QuizStudentsDTO> = (quizStudents+courseStudents+batchStudents).distinctBy { it.id }

    return quizStudentMapper.mapQuizStudent(quiz,students)
    }

}