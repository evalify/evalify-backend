package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.QuizTagsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.StartQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.responses.ResponseDTO
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizSetQuestion
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.domain.QuizTags
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizSetRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.section.domain.DTO.GetSectionDTO
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.random.Random

@Service
@Transactional
class QuizStudentService(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val quizStudentRepository: QuizStudentRepository,
    private val quizSetRepository: QuizSetRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun distributeQuestions(quiz: Quiz, quizSets: List<QuizSet>): List<QuizSetQuestion> {
        // If there's only one set, return its questions
        if (quiz.noOfSets == 1 && quizSets.isNotEmpty()) {
            return quizSets[0].questions.toList()
        }
        
        // For multiple sets, randomly select one set
        val quizNo = Random.nextInt(0, quiz.noOfSets)
        return quizSets.find { it.setNumber == quizNo }?.questions ?: emptyList()
    }

    fun getQuizQuestions(
        quizId: UUID,
        studentId: String?,
        ipAddress: String,
        requestTime: Instant,
        password : String? = null
    ): QuizQuestionReturnDTO {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }

        val user = if(studentId != null) {
            userRepository.findById(studentId).orElseThrow { NotFoundException("User with id $studentId not found") }
        } else{
            throw NotFoundException("Student id cannot be null. Please provide a valid student id.")
        }

        if (requestTime.isBefore(quiz.startTime)) {
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags.map { tags ->
                    QuizTagsReturnDTO(
                        tags.id,
                        tags.name,
                        tags.description
                    )
                },
                questions = emptyList(),
                message = "Quiz has not started yet."
            )
        }
        if (requestTime.isAfter(quiz.endTime)) {
            return QuizQuestionReturnDTO(
                quizTags = quiz.quizTags.map { tags ->
                    QuizTagsReturnDTO(
                        tags.id,
                        tags.name,
                        tags.description
                    )
                },
                questions = emptyList(),
                message = "Quiz has ended."
            )
        }
        val existingQuizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString())
        val quizStudent = if (existingQuizStudent != null) {
            // If quiz is not submitted, update the IP address if needed
            if (!existingQuizStudent.isSubmitted && !existingQuizStudent.ipAddress.contains(ipAddress)) {
                existingQuizStudent.ipAddress.add(ipAddress)
                quizStudentRepository.save(existingQuizStudent)
            }
            existingQuizStudent
        } else {
            // Validate password only when creating new quiz student
            if (quiz.password != null && password != quiz.password) {
                return QuizQuestionReturnDTO(
                    quizTags = quiz.quizTags.map { tags ->
                        QuizTagsReturnDTO(
                            tags.id,
                            tags.name,
                            tags.description
                        )
                    },
                    questions = emptyList(),
                    message = "Wrong password."
                )
            }
            // Only create new record if one doesn't exist
            quizStudentRepository.save(
                QuizStudent(
                    quiz = quiz,
                    student = user,
                    isSubmitted = false,
                    startTime = Instant.now(),
                    duration = quiz.duration,
                    endTime = quiz.endTime,
                    ipAddress = mutableListOf(ipAddress)
                )
            )
        }

        // Get all quiz sets for this quiz
        val quizSets = quizSetRepository.findByQuiz(quiz) ?: emptyList()

        if (quizSets.isEmpty())
            throw NotFoundException("Quiz with id $quizId has no quiz sets.")

        // Get questions based on quiz configuration

            // Distribute questions according to quiz settings
            val selectedQuestions = distributeQuestions(quiz, quizSets)
            
            // Map questions to DTO
            val questions = selectedQuestions.map { question ->
                QuizQuestionsReturnDTO(
                    questions = question.question.question.mapToType(),
                    section = GetSectionDTO(
                        id = question.question.section.id,
                        name = question.question.section.name
                    )
                )
            }


        // Create final response
        return QuizQuestionReturnDTO(
            quizTags = quiz.quizTags.map { tags ->
                QuizTagsReturnDTO(
                    id = tags.id,
                    name = tags.name,
                    description = tags.description
                )
            },
            // Only shuffle if quiz settings allow it
                questions = if (quiz.shuffleQuestions) questions.shuffled() else questions,
            message = "Quiz has started successfully."
        )
    }

    fun saveQuestion(quizId: UUID, studentId: String?, answer: ResponseDTO) {
        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString() ?: "")
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

    fun updateQuiz(quizId: UUID, studentId: String?, responses: List<ResponseDTO>) {
        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString() ?: "")
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

    fun submitQuiz(quizId: UUID, studentId: String?, responses: List<ResponseDTO>) {
        val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId, studentId.toString() ?: "")
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

    fun getQuizTags(quizId: UUID): List<QuizTags> {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }
        return quiz.quizTags
    }




}



