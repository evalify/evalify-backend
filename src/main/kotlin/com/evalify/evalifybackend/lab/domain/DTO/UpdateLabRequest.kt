package com.evalify.evalifybackend.lab.domain.DTO

import java.util.UUID

data class UpdateLabRequest(
    val name: String?,
    val block: String?,
    val ipSubnet: String?
)
