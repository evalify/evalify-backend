package com.evalify.evalifybackend.core.DTO

/**
 * Validation error response DTO
 */
data class ValidationErrorResponseDTO(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val field: String? = null,
    val path: String? = null
)
