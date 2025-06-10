package com.evalify.evalifybackend.batch.domain.DTO

data class SemesterBatchResponse(
    val id: String,
    val name: String,
    val year: String,
    val isActive: Boolean,
)
