package com.evalify.evalifybackend.lab.repository

import com.evalify.evalifybackend.lab.domain.Lab
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "lab")
interface LabRepository : JpaRepository<Lab, UUID> {

    @Query("SELECT l FROM Lab l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.block) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.ipSubnet) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByNameOrBlockOrIpSubnetContainingIgnoreCase(@Param("query") query: String): List<Lab>

    @Query("SELECT l FROM Lab l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.block) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.ipSubnet) LIKE LOWER(CONCAT('%', :query, '%'))")
    fun findByNameOrBlockOrIpSubnetContainingIgnoreCasePage(@Param("query") query: String, pageable: Pageable): Page<Lab>

    override fun findAll(pageable: Pageable): Page<Lab>
}