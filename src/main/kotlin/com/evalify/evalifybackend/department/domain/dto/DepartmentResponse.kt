package com.evalify.evalifybackend.department.domain.dto

import com.evalify.evalifybackend.department.domain.dto.DepartmentBatchResponse
import java.util.*

data class DepartmentResponse(
    val id: UUID? = null,
    val name: String,
    val batches: List<DepartmentBatchResponse> = emptyList()
)

data class SimpleDepartmentResponse(
    val id: UUID,
    val name: String
)