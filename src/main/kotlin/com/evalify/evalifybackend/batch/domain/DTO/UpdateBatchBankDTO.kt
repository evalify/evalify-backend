package com.evalify.evalifybackend.batch.domain.DTO

import java.util.UUID

data class UpdateBatchBankDTO(
    val bank:List<UUID>
) {
}