package com.evalify.evalifybackend.quiz.domain.DTO.sharing

import com.evalify.evalifybackend.bank.domain.BankUser

enum class SharedTags {
   OWNER,SHARED

}


data class SharedUserDTO(
    val user: BankUser,
    val tag : SharedTags

    ) {

}