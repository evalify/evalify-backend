package com.evalify.evalifybackend.bank.controller

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.BankDetailsDTO
import com.evalify.evalifybackend.bank.domain.DTO.CreateBankDTO
import com.evalify.evalifybackend.bank.domain.DTO.CreateQuestionDTO
import com.evalify.evalifybackend.bank.domain.DTO.CreateTopicDTO
import com.evalify.evalifybackend.bank.domain.DTO.ReturnBankQuestionsDTO
import com.evalify.evalifybackend.bank.domain.DTO.SearchTopicDTO
import com.evalify.evalifybackend.bank.domain.DTO.UpdateBankStudentDTO
import com.evalify.evalifybackend.bank.service.BankManagerService
import com.evalify.evalifybackend.bank.service.BankService
import com.evalify.evalifybackend.bank.service.BankStudentService
import com.evalify.evalifybackend.bank.service.BankTopicService
import com.evalify.evalifybackend.quiz.question.domain.Topic
import com.evalify.evalifybackend.quiz.question.domain.bankQuestion.BankQuestion
import com.evalify.evalifybackend.security.utils.SecurityUtils
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/bank")
class BankController(val bankStudentService: BankStudentService, val bankManagerService: BankManagerService,
                     val bankService: BankService, val bankTopicService: BankTopicService
) {


    @PostMapping("/'")
    fun createBank(@RequestBody bank: CreateBankDTO): ResponseEntity<CreateBankDTO> {

        val userId: String? = SecurityUtils.getCurrentUserId()
        val result = bankService.createBank(dto = bank,userId = userId)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/{bankId}/")
    fun deleteBank(@PathVariable bankId: UUID) {
        bankService.deleteBank(bankId)

    }


    @PostMapping("/{bankId}/add-topic")
    fun addTopic(@PathVariable bankId: UUID, @RequestBody topic: CreateTopicDTO):ResponseEntity<CreateTopicDTO> {
        val result = bankTopicService.addTopic(dto = topic , bankId = bankId)
        return ResponseEntity.ok(result)
    }





    @GetMapping("/")
    fun getDetailsOfBank() : ResponseEntity<MutableList<BankDetailsDTO>>{
        val result =  bankManagerService.getDetailsOfBank()
        return ResponseEntity.ok(result)

    }

    @GetMapping("/{bankId}/questions")
    @Transactional
    fun getBankQuestions(@PathVariable bankId: UUID): ResponseEntity<ReturnBankQuestionsDTO> {
        val result = bankManagerService.getBankQuestions(bankId)
        return ResponseEntity.ok(result)
    }


    @GetMapping("/{bankId}/questions/by-topic")
    fun getBankQuestionsByTopic(
        @PathVariable bankId: UUID,
        @RequestBody topicId: List<UUID>
    ): ResponseEntity<List<BankQuestion>> {
        val result = bankManagerService.getQuestionsByTopic(topicId, bankId)
        return ResponseEntity.ok(result)
    }




    @GetMapping("/{bankId}/topics")
    fun getBankTopics(@PathVariable bankId : UUID): List<Topic>?{
        return bankManagerService.getBankTopics(bankId = bankId)
    }



    @PutMapping("/{bankId}/questions/add-question/")
    fun addBankQuestion(@PathVariable bankId:UUID,@RequestBody bankQuestion: CreateQuestionDTO) {


        val userId: String? = SecurityUtils.getCurrentUserId()

        bankManagerService.createBankQuestion(dto = bankQuestion, bankId = bankId, userId = userId)}

    @PatchMapping("/{bankId}/questions/edit-question/{questionId}")
        fun editBankQuestion(@PathVariable bankId: UUID, @PathVariable questionId: UUID) {
            val userId: String? = SecurityUtils.getCurrentUserId()

        }

    @PutMapping("/{bankId}/questions/delete-question/{questionId}")
        fun deleteBankQuestion(@PathVariable bankId: UUID, @PathVariable questionId: UUID) {

        }
    }