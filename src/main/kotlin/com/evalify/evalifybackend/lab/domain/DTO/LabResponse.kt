package com.evalify.evalifybackend.lab.domain.DTO

import java.util.UUID

data class LabResponse(
    val id: UUID?,
    val name: String,
    val block: String,
    val ipSubnet: String,
)
