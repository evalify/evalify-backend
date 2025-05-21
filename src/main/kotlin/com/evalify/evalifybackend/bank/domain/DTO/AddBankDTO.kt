package com.evalify.evalifybackend.bank.domain.DTO

import java.util.UUID

data class AddBankDTO(
    val name: String,
    val description: String,
    val courseCode: String,
    val courseId: UUID,
    val instructorIds: List<UUID>,
    val topicIds: List<UUID>
)
