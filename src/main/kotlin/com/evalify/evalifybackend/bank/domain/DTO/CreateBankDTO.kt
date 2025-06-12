package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.user.domain.User

data class CreateBankDTO(
    val name: String,
    val courseCode: String? = null,
    val semester: Int,


    )
