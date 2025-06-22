package com.evalify.evalifybackend.quiz.service


import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.domain.DTO.BlanksDTO
import com.evalify.evalifybackend.quiz.domain.DTO.CodingReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.DescriptiveReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.FillUpsReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.MCQOptionDTO
import com.evalify.evalifybackend.quiz.domain.DTO.MatchReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.McqReturnDTO
import com.evalify.evalifybackend.quiz.domain.DTO.QuestionsReturnDTO
import com.evalify.evalifybackend.quiz.domain.QuizStudent
import com.evalify.evalifybackend.quiz.question.domain.CodingQuestion
import com.evalify.evalifybackend.quiz.question.domain.DescriptiveQuestion
import com.evalify.evalifybackend.quiz.question.domain.FillUp.FillUp
import com.evalify.evalifybackend.quiz.question.domain.MCQ.MCQ
import com.evalify.evalifybackend.quiz.question.domain.MatchTheFollowing
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.quiz.repository.QuizStudentRepository
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class QuizStudentService(
    val quizRepository: QuizRepository,
    val userRepository: UserRepository,
    val quizStudentRepository: QuizStudentRepository
) {

    fun getQuizQuestions(quizId: UUID,studentId: String,ipAddress:String, requestTime :Instant): List<QuestionsReturnDTO?> {
        val quiz = quizRepository.findById(quizId).orElseThrow{NotFoundException("quiz with id $quizId not found")}
        val user = userRepository.findById(studentId)


        if (requestTime.isAfter(quiz.startTime) && requestTime.isBefore(quiz.endTime)){
            //First time entering into a quiz
            val quizStudent = quizStudentRepository.findByQuizIdAndStudentId(quizId,studentId) ?: quizStudentRepository.save(
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

            //Add ip address to the list
            if(!quizStudent.ipAddress.contains(ipAddress)){
                quizStudent.ipAddress.add(ipAddress)
                quizStudentRepository.save(quizStudent);
            }


            val returnQuestions: List<QuizQuestion> = quizStudent.quiz.section.flatMap { it.quizQuestions }

            val finalQuestions = if(quiz.shuffleQuestions) returnQuestions.shuffled() else returnQuestions

            return finalQuestions.map { quizQuestion ->
                val baseQuestion = quizQuestion.question
                baseQuestion?.mapToType()
            }


        }
        else{
            return emptyList()
        }

    }}











