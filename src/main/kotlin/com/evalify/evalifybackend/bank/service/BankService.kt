package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.DTO.CreateBankDTO
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.semester.service.SemesterService
import com.evalify.evalifybackend.usewr.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID


@Service
class BankService(private val bankRepository: BankRepository, private val userRepository: UserRepository) {

    fun createBank(dto : CreateBankDTO , userId : String?) : CreateBankDTO {

        if(userId == null) throw RuntimeException("User id cannot be null")

        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }

        val bank = Bank(
                name = dto.name,
                semester = dto.semester,
                createdAt = Instant.now(),
                createdBy = user
                ,courseCode = dto.courseCode

            )
        val savedBank = bankRepository.save(bank)
            return CreateBankDTO(
                name = savedBank.name,
                courseCode = savedBank.courseCode,
                semester = savedBank.semester,


            )
    }

    fun deleteBank(bankId: UUID) {

        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }

        bankRepository.deleteById(bankId)
    }


    fun editBank(dto : CreateBankDTO , bankId : UUID , userId : String?): CreateBankDTO{
        if(userId == null) throw RuntimeException("User id cannot be null")

        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }

        val bank = Bank(
            id = bankId,
            name = dto.name,
            semester = dto.semester,

            courseCode = dto.courseCode
        )

        val savedBank = bankRepository.save(bank)
        return CreateBankDTO(
            name = savedBank.name,
            courseCode = savedBank.courseCode,
            semester = savedBank.semester,
            )

    }

}