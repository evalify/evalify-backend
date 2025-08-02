package com.evalify.evalifybackend.section.domain.DTO

import com.evalify.evalifybackend.section.domain.Section
import java.util.UUID

data class GetSectionDTO(
    val id : UUID?,
    val name : String

) {
}