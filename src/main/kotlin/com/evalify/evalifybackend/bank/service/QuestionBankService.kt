package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.BankQuestion
import com.evalify.evalifybackend.bank.domain.DTO.AddBankQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.UpdateBankQuestionDTO
import com.evalify.evalifybackend.bank.repository.BankQuestionRepo
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.course.repository.CourseRepository
import com.evalify.evalifybackend.topic.repository.TopicRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuestionBankService(
    val bankRepo: BankRepository,
    val bankQuestionRepo: BankQuestionRepo,
    val userRepo: BankQuestionRepo,
    val courseRepo: CourseRepository,
    val topicRepo: TopicRepository,
    private val topicRepository: TopicRepository
) {
    fun createQuestionBank(bank: Bank) {
        bankRepo.save(bank)
    }

    fun updateQuestionBank(updatedBank: Bank) {
        bankRepo.save(updatedBank)
    }

    fun deleteQuestionBank(bankID: UUID) {
        bankRepo.deleteById(bankID)
    }

    fun createQuestion(addBankQuestionDTO: AddBankQuestionDTO) {
        val banks = bankRepo.findById(addBankQuestionDTO.BankId).
        orElseThrow { IllegalArgumentException("Bank not found") }

        val topics = topicRepo.findAllById(addBankQuestionDTO.topicIds).toMutableList()

        val question = BankQuestion(
            question = addBankQuestionDTO.question,
                                    bank = banks,
                                    topic = topics,
                                    explanation = addBankQuestionDTO.explanation,
                                    hint = addBankQuestionDTO.hint,
                                    type = addBankQuestionDTO.type,
                                    marks = addBankQuestionDTO.marks,
                                    bloomsTaxonomy = addBankQuestionDTO.bloomsTaxonomy,
                                    co = addBankQuestionDTO.co,
                                    negativeMark = addBankQuestionDTO.negativeMark,
                                    difficulty = addBankQuestionDTO.difficulty,
                                    answer = addBankQuestionDTO.answer)
        banks.questions.add(question)
        bankQuestionRepo.save(question)


    }


    fun updateQuestion(updateBankQuestionDTO: UpdateBankQuestionDTO, questionId: UUID) {
         val existingQuestion = bankQuestionRepo.findById(questionId)
             .orElseThrow{IllegalArgumentException("Question not found")}


         val topics = topicRepo.findAllById(updateBankQuestionDTO.topicIds).toMutableList()

        existingQuestion.apply {
            question = updateBankQuestionDTO.question
            explanation = updateBankQuestionDTO.explanation
            hint = updateBankQuestionDTO.hint
            type = updateBankQuestionDTO.type
            marks = updateBankQuestionDTO.marks
            bloomsTaxonomy = updateBankQuestionDTO.bloomsTaxonomy
            co = updateBankQuestionDTO.co
            negativeMark = updateBankQuestionDTO.negativeMark
            difficulty = updateBankQuestionDTO.difficulty
            answer = updateBankQuestionDTO.answer
            topic.clear()
            topic.addAll(topics)
        }

        bankQuestionRepo.save(existingQuestion)


    }

    fun deleteQuestion(questionId: UUID) {
        val question = bankQuestionRepo.findById(questionId)
            .orElseThrow{IllegalArgumentException("Question not found")}
        val bank = question.bank


        bank.questions.removeIf { it.id == questionId }

        bankQuestionRepo.delete(question)

    }




}