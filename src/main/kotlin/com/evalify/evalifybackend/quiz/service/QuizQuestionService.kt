package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.quiz.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.quiz.question.repository.BankQuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuestionRepository
import com.evalify.evalifybackend.quiz.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.questions.domain.BaseQuestion
import com.evalify.evalifybackend.questions.domain.Difficulty
import com.evalify.evalifybackend.questions.domain.QuestionTypes
import com.evalify.evalifybackend.quiz.domain.DTO.AddQuestionsResponse
import com.evalify.evalifybackend.quiz.domain.Quiz
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.section.repository.SectionRepository
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
    val quizQuestionRepository: QuizQuestionRepository,
    val questionRepository: QuestionRepository,
    val userRepository: UserRepository,
    val topicRepo: TopicRepo,
    val sectionRepository: SectionRepository
) {



    @Transactional
    open fun addByQuestionByFilters(
        quizId: UUID, topicId: List<UUID>?, difficulty: List<Difficulty>?, noOfQuestion: Int?,
        questionTypes: List<QuestionTypes>?, userId: UUID, bankIds: List<UUID>?,sectionId : UUID
    ): AddQuestionsResponse {


        val filteredTopics: MutableList<BankQuestion> = mutableListOf()
        val filteredLevels: MutableList<BankQuestion> = mutableListOf()
        val filteredQuestionTypes: MutableList<BankQuestion> = mutableListOf()

        val quiz: Quiz = quizRepository.findById(quizId).orElseThrow {
            NotFoundException("Quiz with id $quizId not found")
        }
        val user = userRepository.getReferenceById(userId)
        val section = sectionRepository.findById(sectionId).orElseThrow{
            NotFoundException("Quiz with id $sectionId not found")
        }

        // for unique addition to the quiz from the bank questions
        val existingQuestionIds: Set<UUID> = section.quizQuestions.map{ it.bankQuestion.id }.toSet()

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
                bankQuestion.question.getQuestionType() in questionTypes
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

            //Creating a quiz question
            val dupQuestion = bankQuestion.question.copyQuestion()

            //duplicating a question
            val duplicatedQuestion = questionRepository.save(dupQuestion)

            //creating a quiz question object
            val quizQuestions = QuizQuestion(
                question = duplicatedQuestion,
                section = section,
                updateBy = user,
                bankQuestion = bankQuestion
            )
            //saving to quiz question table
            val savedQuizQuestion = quizQuestionRepository.save(quizQuestions)
            section.quizQuestions.add(savedQuizQuestion)
        }

            sectionRepository.save(section)

        return AddQuestionsResponse(
            quizId = quiz.id,
            sectionId = sectionId,
            addedQuestionCount = filteredQuestions.size,
            addedBankQuestionIds = bankQuestionsId,
            message = "Questions added successfully"
        )
    }
}



