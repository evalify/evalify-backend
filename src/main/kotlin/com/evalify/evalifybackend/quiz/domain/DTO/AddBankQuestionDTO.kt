package com.evalify.evalifybackend.quiz.domain.DTO

import java.util.UUID

data class AddBankQuestionDTO(
    val section_id: UUID,
    val user_id: UUID,
    val bank_id:List<UUID>
) {
}