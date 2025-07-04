package com.evalify.evalifybackend.lab.domain.DTO

data class CreateLabRequest(
    val name: String,
    val block: String,
    val ipSubnet: String
)
