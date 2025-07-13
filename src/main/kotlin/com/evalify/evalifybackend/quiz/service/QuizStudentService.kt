package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizSetQuestion
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizSetRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import java.time.Instant
import java.util.*
import kotlin.random.Random

@Service
@Transactional
class QuizStudentService(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val quizStudentRepository: QuizStudentRepository,
    private val quizSetRepository: QuizSetRepository
) {

    fun distributeQuestions(user: Optional<User?>, quizSet: List<QuizSet>?, noOfSets: Int): MutableList<QuizSetQuestion>? {
        val quizNo = Random.nextInt(0, noOfSets) // 0-based index
        val selectedSet = quizSetRepository.findBySetNumber(quizNo)
        return selectedSet?.questions
    }

    fun getQuizQuestions(
        quizId: UUID,
        studentId: String,
        ipAddress: String,
        requestTime: Instant,
        dto : StartQuizDTO
    ): QuizQuestionReturnDTO? {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }

        val user = userRepository.findById(studentId)

        if (requestTime.isBefore(quiz.startTime)) {
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags,
                questions = emptyList(),
                message = "Quiz has not started yet."
            )
        }
        if (requestTime.isAfter(quiz.endTime)) {
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags,
                questions = emptyList(),
                message = "Quiz has ended."
            )
        }
        if(dto.password == quiz.password){
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags,
                questions = emptyList(),
                message = "Wrong password."
            )
        }

        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId)
            ?: quizStudentRepository.save(
                QuizStudent(
                    quiz = quiz,
                    student = user.get(),
                    isSubmitted = false,
                    startTime = Instant.now(),
                    duration = quiz.duration,
                    endTime = null,
                    ipAddress = mutableListOf(ipAddress)
                )
            )

        if (!quizStudent.ipAddress.contains(ipAddress)) {
            quizStudent.ipAddress.add(ipAddress)
            quizStudentRepository.save(quizStudent)
        }

        val quizSet = quizSetRepository.findByQuiz(quiz)
        val finalQuestions = distributeQuestions(user, quizSet, quiz.noOfSets)
        val quizTags = quiz.quizTags

        val questions =  finalQuestions?.map { question ->
            QuizQuestionsReturnDTO(
                questions = question.question.question.mapToType(quiz.shuffleOptions),
                section = question.question.section
            )
        } ?: emptyList()
        return QuizQuestionReturnDTO(
            quizTags = quizTags,
            questions = questions

        )
    }

    fun saveQuestion(quizId: UUID, studentId: String, answer: ResponseDTO) {
        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId)
            ?: throw NotFoundException("QuizStudent record not found")
        val responses = quizStudent.responses
        val existingIndex = responses.indexOfFirst { it.questionId == answer.questionId }
        if (existingIndex != -1) {
            responses[existingIndex] = answer
        } else {
            responses.add(answer)
        }
        quizStudentRepository.save(quizStudent)
    }

    fun updateQuiz(quizId: UUID, studentId: String, responses: List<ResponseDTO>) {
        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId)
            ?: throw NotFoundException("Quiz with id $quizId not found")

        val existingResponses = quizStudent.responses

        responses.forEach { newResponse ->
            val index = existingResponses.indexOfFirst { it.questionId == newResponse.questionId }
            if (index != -1) {
                existingResponses[index] = newResponse
            } else {
                existingResponses.add(newResponse)
            }
        }

        quizStudentRepository.save(quizStudent)
    }




}



