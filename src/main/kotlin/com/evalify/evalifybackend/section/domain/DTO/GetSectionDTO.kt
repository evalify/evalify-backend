package com.evalify.evalifybackend.section.domain.DTO

import com.evalify.evalifybackend.section.domain.Section

data class GetSectionDTO(
    val section : List<Section>
) {
}