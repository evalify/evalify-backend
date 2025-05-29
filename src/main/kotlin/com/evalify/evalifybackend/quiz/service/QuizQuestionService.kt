package com.evalify.evalifybackend.quiz.service

import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.question.domain.quizQuestion.QuizQuestion
import com.evalify.evalifybackend.question.repository.QuestionRepository
import com.evalify.evalifybackend.question.repository.QuizQuestionRepository
import com.evalify.evalifybackend.quiz.repository.QuizRepository
import com.evalify.evalifybackend.section.repository.SectionRepository
import com.evalify.evalifybackend.usewr.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.stereotype.Service
import java.util.UUID

@Service
open class QuizQuestionService(
    val quizQuestionRepository: QuizQuestionRepository,
    val sectionRepository: SectionRepository,
    val bankRepository: BankRepository,
    val questionRepository: QuestionRepository,
    val userRepository: UserRepository

) {
    @Transactional
    @Modifying
   open fun addQuestionsFromBank(bankIds:List<UUID>,sectionId:UUID,userId: UUID){

       //updated user
       val updatedUser = userRepository.findById(userId).orElseThrow{
           NotFoundException("User with id $userId not found")
       }

       //quiz reference
       val section = sectionRepository.findById(sectionId).orElseThrow{
           NotFoundException("Quiz with id $sectionId not found")
       }

        //banks
        val banks = bankRepository.findAllById(bankIds)
        //bank question
        val bankQuestions = banks.flatMap { bank ->bank.bankQuestion}
        System.out.println(bankQuestions)
        for (bankQuestion in bankQuestions) {
            //Creating a quiz question
            var dupQuestion = bankQuestion.question
            dupQuestion.id = null
            System.out.println(dupQuestion.question)


            //duplicating a question
            val duplicatedQuestion = questionRepository.save(dupQuestion)
            System.out.println(duplicatedQuestion.question)

            //creating a quiz question object
            val quizQuestion = QuizQuestion(
                question = duplicatedQuestion,
                section = section,
                updateBy = updatedUser,
                bankQuestion = bankQuestion
            )

            //saving to quiz question table
            val savedQuizQuestion = quizQuestionRepository.save(quizQuestion)

            //adding quiz question to existing quiz
            section.quizQuestions.add(savedQuizQuestion)
        }
        sectionRepository.save(section)
    }
}