package com.devlabs.devlabsbackend.batch.domain.dto

data class SemesterBatchResponse(
    val id: String,
    val name: String,
    val year: String,
    val isActive: Boolean,
)
