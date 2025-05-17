package com.evalify.evalifybackend.bank.controller

import com.evalify.evalifybackend.bank.domain.DTO.UpdateBankStudentDTO
import com.evalify.evalifybackend.bank.service.BankStudentService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("bank")
class BankController(val bankStudentService: BankStudentService) {
    @PutMapping("{bankId}/add-student")
    fun assignStudentToBank(@RequestBody studentDTO:UpdateBankStudentDTO,@PathVariable bankId:UUID){
        bankStudentService.addStudentToBank(bankId = bankId, studentId = studentDTO.studentId)
    }
    @PutMapping("{bankId}/remove-student")
    fun removeStudentFromBank(@RequestBody studentDTO:UpdateBankStudentDTO,@PathVariable bankId:UUID){
        bankStudentService.removeStudentToBank(bankId = bankId, studentId = studentDTO.studentId)
    }
}