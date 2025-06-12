package com.evalify.evalifybackend.bank.domain.DTO

import com.evalify.evalifybackend.semester.domain.Semester
import com.evalify.evalifybackend.user.domain.User
import java.util.UUID

data class BankDetailsDTO(
    val course : String?,
    val bankId : UUID?,
    val name : String,
    val semester: String,
    val topics : Int?,
    val questions : Int,
    val access : List<AccessDTO>

) {
}