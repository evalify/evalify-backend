package com.evalify.evalifybackend.section.repository

import com.evalify.evalifybackend.section.domain.Section
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "section")
interface SectionRepository : JpaRepository<Section, UUID>{
}