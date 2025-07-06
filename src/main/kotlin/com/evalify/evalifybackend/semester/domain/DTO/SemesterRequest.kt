package com.evalify.evalifybackend.semester.domain.DTO

class SemesterRequest(
    val name: String,
    val year: Int,
    val isActive: Boolean = true
) {
}