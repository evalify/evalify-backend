package com.evalify.evalifybackend.lab.repository

import com.evalify.evalifybackend.lab.domain.Lab
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "lab")
interface LabRepository : JpaRepository<Lab,UUID> {
}