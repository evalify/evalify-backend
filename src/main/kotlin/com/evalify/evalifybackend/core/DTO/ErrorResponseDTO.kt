package com.evalify.evalifybackend.core.DTO

import java.time.Instant

/**
 * Standard error response DTO
 */
data class ErrorResponseDTO(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String? = null,
    val details: Map<String, Any>? = null
)
