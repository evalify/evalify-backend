package com.evalify.evalifybackend.batch.domain.DTO

import java.time.Year
import java.util.UUID

data class CreateBatchRequest(
    val name: String,
    val graduationYear: Year,
    val section: String,
    val isActive: Boolean,
    val departmentId: UUID? = null
)

data class UpdateBatchRequest(
    val name: String? = null,
    val graduationYear: Year? = null,
    val section: String? = null,
    val isActive: Boolean? = null,
    val departmentId: UUID? = null
)
