package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.question.repository.QuestionRepository
import com.evalify.evalifybackend.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import java.util.UUID

open class QuizQuestionService(
    val quizQuestionRepository: QuizQuestionRepository,
    val quizRepository: QuizRepository,
    val bankRepository: BankRepository,
    val questionRepository: QuestionRepository,
    val userRepository: UserRepository

) {
    @Transactional
   open fun addQuestionsFromBank(bankIds:List<UUID>,quizId:UUID,userId: UUID){

       //bank
        val banks = bankRepository.findAllById(bankIds)

        //mapping to bank questions
        val questions = banks.mapNotNull { it.bankQuestion }

        //flattening the bank questions
        val finalQuestions = questions.flatten()

        //reference of quiz
        var quiz: Quiz = quizRepository.findById(quizId).orElseThrow{
            NotFoundException("Quiz with id $quizId not found")
        }

        //reference of user
        val user = userRepository.getReferenceById(userId)

        finalQuestions.stream().forEach {bankQuestion -> {

            val question = bankQuestion.question
           question.id = null
           val savedQuestion:BaseQuestion = questionRepository.save(question)
            var quizQuestion = QuizQuestion(question=savedQuestion, quiz = quiz, bankQuestion = bankQuestion, updateBy = user)
            quiz.quizQuestion.add(quizQuestion)
        }}

        quizRepository.save(quiz)

    }
}