package com.evalify.evalifybackend.department.domain.dto

import com.evalify.evalifybackend.department.domain.dto.DepartmentBatchResponse
import java.util.*

data class DepartmentResponse(
    val id: String? = null,
    val name: String,
    val batches: List<DepartmentBatchResponse> = emptyList()
)

data class SimpleDepartmentResponse(
    val id: String? = null,
    val name: String
)