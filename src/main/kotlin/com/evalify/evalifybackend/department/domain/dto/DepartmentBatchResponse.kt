package com.evalify.evalifybackend.department.domain.dto

import jakarta.persistence.Id
import java.time.Year
import java.util.UUID

data class DepartmentBatchResponse(
    val id: UUID? = null,
    val name: String,
    val graduationYear: Year? = null,
    val section: String,
)
