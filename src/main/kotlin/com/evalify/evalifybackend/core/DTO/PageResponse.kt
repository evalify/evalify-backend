package com.evalify.evalifybackend.core.DTO

/** Generic pagination response wrapper */
data class PageResponse<T>(
        val content: List<T>,
        val page: Int,
        val size: Int,
        val totalElements: Long,
        val totalPages: Int,
        val first: Boolean,
        val last: Boolean,
        val hasNext: Boolean,
        val hasPrevious: Boolean
)
