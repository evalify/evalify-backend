package com.evalify.evalifybackend.core.DTO

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

/** Pagination request parameters */
data class PageRequest(
        @field:Min(value = 0, message = "Page number must be >= 0") val page: Int = 0,
        @field:Min(value = 1, message = "Page size must be >= 1")
        @field:Max(value = 100, message = "Page size must be <= 100")
        val size: Int = 20,
        val sortBy: String? = null,
        val sortDirection: String? = "ASC"
)
