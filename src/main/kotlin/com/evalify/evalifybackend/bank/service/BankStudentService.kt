package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.core.exception.NotFoundException
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BankStudentService(private val bankRepository: BankRepository,private val userRepository: UserRepository) {
    fun addStudentToBank(bankId:UUID,studentId:List<UUID>){
        val bank = bankRepository.findById(bankId).orElseThrow{
            NotFoundException("bank with id $bankId not found")
        }
        val student = userRepository.findAllById(studentId)
        bank.student.addAll(student)
        bankRepository.save(bank)
    }

    fun removeStudentToBank(bankId:UUID,studentId:List<UUID>){
        val bank = bankRepository.findById(bankId).orElseThrow{
            NotFoundException("bank with id $bankId not found")
        }
        val student = userRepository.findAllById(studentId)
        bank.student.removeAll(student)
        bankRepository.save(bank)
    }
}