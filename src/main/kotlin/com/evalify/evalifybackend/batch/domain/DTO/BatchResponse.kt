package com.evalify.evalifybackend.batch.domain.DTO

import com.evalify.evalifybackend.department.domain.dto.DepartmentResponse
import java.time.Year
import java.util.UUID

data class BatchResponse(
    val id: UUID?,
    val name: String,
    val graduationYear: Year,
    val section: String,
    val isActive: Boolean,
    val department: DepartmentResponse?
)
