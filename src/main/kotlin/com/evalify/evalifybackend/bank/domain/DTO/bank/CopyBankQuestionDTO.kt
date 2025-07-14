package com.evalify.evalifybackend.bank.domain.DTO.bank

import java.util.UUID

data class CopyBankQuestionDTO(
    val bankId : UUID,
    val questionIds : List<UUID>,
    val move : Boolean,
    val createNewTopic : Boolean

    ) {
}