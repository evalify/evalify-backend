package com.evalify.evalifybackend.bank.domain.DTO.bank

data class CreateBankDTO(
    val name: String,
    val courseCode: String? = null,
    val semester: Int,


    )
