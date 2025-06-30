package com.evalify.evalifybackend.bank.domain.DTO.bank

import com.evalify.evalifybackend.bank.domain.DTO.AccessDTO
import java.util.UUID

data class BankDetailsDTO(
    val courseCode : String?,
    val id : UUID?,
    val name : String,
    val semester: String,
    val topics : Int?,
    val questions : Int,
    val access : List<AccessDTO>

) {
}