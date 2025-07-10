package com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding

enum class TestCaseType {
    SAMPLE,
    HIDDEN
}

data class TestCaseDTO(
    val input: List<Any>,
    val expected: Any,
    val tags : TestCaseType,
    val isMinimal : Boolean,
    val language: String,){
}