package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.question.repository.QuestionRepository
import com.evalify.evalifybackend.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.AddQuestionsResponse
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.topic.repository.TopicRepo
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.CompletableFuture.anyOf

@Service
open class QuizQuestionService(
    val quizRepository: QuizRepository,
    val bankRepository: BankRepository,
    val questionRepository: QuestionRepository,
    val userRepository: UserRepository,
    val topicRepo: TopicRepo
) {

    @Transactional
    open fun addQuestionsFromBank(bankIds: List<UUID>, quizId: UUID, userId: UUID) {

        val banks = bankRepository.findAllById(bankIds)
        val questions = banks.mapNotNull { it.bankQuestion }
        val finalQuestions = questions.flatten()

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }

        val user = userRepository.getReferenceById(userId)

        finalQuestions.forEach { bankQuestion ->
            val question = bankQuestion.question
            question.id = null
            val savedQuestion: BaseQuestion = questionRepository.save(question)
            val quizQuestion = QuizQuestion(
                question = savedQuestion,
                quiz = quiz,
                bankQuestion = bankQuestion,
                updateBy = user
            )
            quiz.quizQuestion.add(quizQuestion)
        }

        quizRepository.save(quiz)
    }

    @Transactional
    open fun addByTopic(quizId: UUID, topicId: List<UUID>, userId: UUID, bankIds: List<UUID>) {

        val topics = topicRepo.findAllById(topicId)

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }

        val finalQuestions: MutableList<BankQuestion> = mutableListOf()
        val user = userRepository.getReferenceById(userId)

        bankRepository.findAll().forEach { bank ->
            val questions = bank.bankQuestion.filter { bankQuestion ->
                bankQuestion.question.topic.any { it in topics }
            }
            finalQuestions.addAll(questions)
        }

        finalQuestions.forEach { bankQuestion ->
            val question = bankQuestion.question
            question.id = null
            val savedQuestion: BaseQuestion = questionRepository.save(question)
            val quizQuestion =
                QuizQuestion(question = savedQuestion, quiz = quiz, bankQuestion = bankQuestion, updateBy = user)
            quiz.quizQuestion.add(quizQuestion)
        }

        quizRepository.save(quiz)
    }


    @Transactional
    open fun addByQuestionByFilters(
        quizId: UUID, topicId: List<UUID>?, difficulty: List<Difficulty>?, noOfQuestion: Int?,
        questionTypes: List<QuestionTypes>?, userId: UUID, bankIds: List<UUID>?
    ): AddQuestionsResponse {


        val filteredTopics: MutableList<BankQuestion> = mutableListOf()
        val filteredLevels: MutableList<BankQuestion> = mutableListOf()
        val filteredQuestionTypes: MutableList<BankQuestion> = mutableListOf()

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }
        val user = userRepository.getReferenceById(userId)

        // for unique addition to the quiz from the bank questions
        val existingQuestionIds: Set<UUID> = quiz.quizQuestion.map { it.bankQuestion.id }.toSet()

        var count : Int



        val banks = if (bankIds != null) {
            bankRepository.findAllById(bankIds)
        } else {
            bankRepository.findAll()
        }

        val topics = if (topicId != null) {
            topicRepo.findAllById(topicId)
        } else {
            topicRepo.findAll()
        }
        banks.forEach { bank ->
            val questions = bank.bankQuestion.filter { bankQuestion ->
                bankQuestion.id !in existingQuestionIds &&
                        bankQuestion.question.topic.any { it in topics }
            }
            filteredTopics.addAll(questions)
        }


        if (difficulty != null) {
            filteredLevels.addAll(filteredTopics.filter { bankQuestion ->
                bankQuestion.question.difficulty in difficulty
            })
        } else {
            filteredLevels.addAll(filteredTopics)
        }

        if (questionTypes != null) {

            filteredQuestionTypes.addAll(filteredLevels.filter { bankQuestion ->
                bankQuestion.question.type in questionTypes
            })
        } else {
            filteredQuestionTypes.addAll(filteredLevels)
        }

        if(noOfQuestion != null)
        {
            count = noOfQuestion
            val message = if ((noOfQuestion ) > filteredQuestionTypes.size) {
                count = filteredQuestionTypes.size
                "Only ${filteredQuestionTypes.size} questions added. Not enough questions are available."
            } else {
                "Successfully added ${filteredQuestionTypes.size} questions to quiz."
            }
        }else{
            count = filteredQuestionTypes.size
        }



        val filteredQuestions = filteredQuestionTypes.shuffled().take(count)
        val bankQuestionsId = filteredQuestions.map {bankQuestion -> bankQuestion.id}

        filteredQuestions.forEach { bankQuestion ->
            val question = bankQuestion.question
            question.id = null
            val savedQuestion: BaseQuestion = questionRepository.save(question)
            val quizQuestion =
                QuizQuestion(question = savedQuestion, quiz = quiz, bankQuestion = bankQuestion, updateBy = user)
            quiz.quizQuestion.add(quizQuestion)
        }

        quizRepository.save(quiz)

        return AddQuestionsResponse(
            quizId = quiz.id,
            addedQuestionCount = filteredQuestions.size,
            addedBankQuestionIds = bankQuestionsId,
            message = "Questions added successfully"
        )
    }
}




