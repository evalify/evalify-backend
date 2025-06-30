package com.evalify.evalifybackend.bank.service

import com.evalify.evalifybackend.bank.domain.Bank
import com.evalify.evalifybackend.bank.domain.BankUser
import com.evalify.evalifybackend.bank.domain.BankUserId
import com.evalify.evalifybackend.bank.domain.DTO.bank.CreateBankDTO
import com.evalify.evalifybackend.bank.repository.BankRepository
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.GetSharedUsersDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.ShareQuizDTO
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedTags
import com.evalify.evalifybackend.quiz.domain.DTO.sharing.SharedUserDTO
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
                courseCode = dto.courseCode

            )
        val bankUser = BankUser(
            id = BankUserId(bank.id,user.id),
            bank = bank,
            user = user,
            tags = SharedTags.OWNER
        )
        bank.sharedUsers.add(bankUser)


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

    fun shareBank(bankId: UUID, dto: ShareQuizDTO) {
        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }

        val user = userRepository.findAllById(dto.userID)

        user.map{
                user->
            val bankUser = BankUser(
                id = BankUserId(bankId, user.id),
                bank = bank,
                user = user,
                tags = SharedTags.SHARED
            )
            bank.sharedUsers.add(bankUser)
            bankRepository.save(bank)

        }


    }

    fun unshareBank(bankId: UUID, dto: ShareQuizDTO) {
        val bank = bankRepository.findById(bankId).orElseThrow { RuntimeException("Bank not found") }
        val user = userRepository.findAllById(dto.userID)

        bank.sharedUsers.removeIf { it.id.userId == user[0].id }
        bankRepository.save(bank)
    }

    fun getShareBank(bankId: UUID) : GetSharedUsersDTO {
        val bank = bankRepository.findById(bankId)
        val users = bank.get().sharedUsers.map{
            user ->
            SharedUserDTO(
                user = user,
                tag = user.tags
            )
        }

        return GetSharedUsersDTO(users)

    }


}