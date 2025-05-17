package com.evalify.evalifybackend.batch.service

import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.batch.repository.BatchRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BatchBankService (private val batchRepository: BatchRepository,private val bankRepository: BankRepository){
    fun addBank(batchId:UUID,bankId:List<UUID>){
        val batch = batchRepository.findById(batchId).orElseThrow{
            NotFoundException("batch with id $batchId not found")
        }
        val banks = bankRepository.findAllById(bankId)
        batch.bank.addAll(banks);
        batchRepository.save(batch)
    }
    fun removeBank(batchId:UUID,bankId:List<UUID>){
        val batch = batchRepository.findById(batchId).orElseThrow{
            NotFoundException("batch with id $batchId not found")
        }
        val banks = bankRepository.findAllById(bankId)
        batch.bank.removeAll(banks);
        batchRepository.save(batch)
    }
}