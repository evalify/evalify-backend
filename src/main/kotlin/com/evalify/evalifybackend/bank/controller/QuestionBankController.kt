package com.evalify.evalifybackend.bank.controller

import com.evalify.evalifybackend.answer.DTO.AnswerDTO
import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.BankQuestion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import com.evalify.evalifybackend.bank.service.QuestionBankService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.evalify.evalifybackend.bank.domain.DTO.AddBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.AddBankQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.AddInstructorsDTO
import com.evalify.evalifybackend.bank.domain.DTO.UpdateBankQuestionDTO
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.PathVariable

import java.util.UUID


@RestController
@RequestMapping("/bank")
class QuestionBankController(val qbService: QuestionBankService) {

    @Autowired
    val userRepo : UserRepository? = null

    @PostMapping("/bank")
    fun createBank(@RequestBody bank: Bank  ){
        qbService.createQuestionBank(bank)
    }

    @PutMapping("/bank/{bankID}")
    fun updateBank(@RequestBody updatedBank: Bank, @PathVariable bankID: UUID){
        qbService.updateQuestionBank(updatedBank)

    }

    @DeleteMapping("/bank/{bankID}")
    fun deleteBank(@PathVariable bankID: UUID){
        qbService.deleteQuestionBank(bankID)
    }

    @PostMapping("/bank/bank-question/")
    fun createQuestion(@RequestBody addBankQuestionDTO: AddBankQuestionDTO) {
        qbService.createQuestion(addBankQuestionDTO)
    }

    @PutMapping("/bank/bank-question/{questionId}")
    fun updateQuestion(@RequestBody updateBankQuestionDTO: UpdateBankQuestionDTO, @PathVariable questionId: UUID){
        qbService.updateQuestion(updateBankQuestionDTO,questionId)
    }

    @DeleteMapping("/bank/bank-question/{questionId}")
    fun deleteQuestion(@PathVariable questionId: UUID){
        qbService.deleteQuestion(questionId)
    }

    @PutMapping("/bank/add-instructor/{bankId}")
    fun addInstructor(@RequestBody addInstructorsDTO: AddInstructorsDTO, @PathVariable bankId: UUID){
        qbService.addInstructorBank(instructorID = addInstructorsDTO, bankId = bankID)
    }

    @PutMapping("/bank/remove-instructor/{bankId}")
    fun removeInstructor(@RequestBody addInstructorsDTO: AddInstructorsDTO,@PathVariable bankId: UUID){
        //Here also cascade update.
    }

    @PutMapping("/bank/bank-question/add-answer/{bankId}/{questionId}")
    fun addAnswer(@RequestBody answerDTO: AnswerDTO, @PathVariable bankId: UUID, @PathVariable questionId: UUID){
        //will update the question as well as in the bank
    }

    @PutMapping("/bank/bank-question/remove-answer/{bankId}/{questionId}")
    fun removeAnswer(@RequestBody answerDTO: AnswerDTO, @PathVariable bankId: UUID, @PathVariable questionId: UUID){
        //will update the question as well as in the bank
    }




}



