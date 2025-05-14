package com.evalify.evalifybackend.semester.repository

import com.evalify.evalifybackend.semester.domain.Semester
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "semester", collectionResourceRel = "semester")
interface SemesterRepository : JpaRepository<Semester,UUID>{

}