package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.crud.quiz.QuizQuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.QuizSet
import com.evalify.evalifybackend.quiz.domain.QuizSetQuestion
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizSetRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.user.domain.User
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import kotlin.random.Random

@Service
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
        requestTime: Instant
    ): List<QuizQuestionsReturnDTO> {
        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NotFoundException("Quiz with id $quizId not found") }

        val user = userRepository.findById(studentId)

        if (requestTime.isAfter(quiz.startTime) && requestTime.isBefore(quiz.endTime)) {
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

            return finalQuestions?.map { question ->
                QuizQuestionsReturnDTO(
                    questions = question.question.question.mapToType(quiz.shuffleOptions),
                    section = question.question.section
                )
            } ?: emptyList()
        }

        return emptyList()
    }
    //TODO() add the logic for distributing the sets serially according to the roll number




}



