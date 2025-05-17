package com.evalify.evalifybackend.batch.controller

import com.evalify.evalifybackend.batch.domain.DTO.UpdateBatchBankDTO
import com.evalify.evalifybackend.batch.service.BatchBankService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("batch")
class BatchController(val batchBankService: BatchBankService) {
    @PutMapping("{batchId}/add-bank")
    fun addBankToBatch(@RequestBody bankDto:UpdateBatchBankDTO,@PathVariable batchId:UUID){
        batchBankService.addBank(batchId = batchId, bankId = bankDto.bank)
    }
    @PutMapping("{batchId}/remove-bank")
    fun removeBankFromCourse(@RequestBody bankDto:UpdateBatchBankDTO,@PathVariable batchId:UUID){
        batchBankService.removeBank(batchId = batchId, bankId = bankDto.bank)
    }
}