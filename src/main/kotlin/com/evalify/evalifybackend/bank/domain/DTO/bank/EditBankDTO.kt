package com.evalify.evalifybackend.bank.domain.DTO.bank

data class EditBankDTO(
    val name: String? = null,
    val courseCode: String? = null,
    val semester: Int? = null,
) {
}