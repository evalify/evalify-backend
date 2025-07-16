package com.evalify.evalifybackend.bank.domain.DTO.questionTypes.coding

enum class TestCaseType {
    SAMPLE,
    HIDDEN
}

data class TestCaseDTO(
    val code : String,
    val tags : TestCaseType,
    val isMinimal : Boolean,
    val language: String,){
}