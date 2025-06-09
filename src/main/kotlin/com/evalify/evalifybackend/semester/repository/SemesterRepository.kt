package com.evalify.evalifybackend.semester.repository

import com.evalify.evalifybackend.semester.domain.Semester
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import java.util.UUID

@RepositoryRestResource(path = "semester")
interface SemesterRepository : JpaRepository<Semester, UUID> {

    @Query("SELECT s FROM Semester s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR CAST(s.year AS string) LIKE CONCAT('%', :query, '%')")
    fun findByNameOrYearContainingIgnoreCase(@Param("query") query: String): List<Semester>

    @Query("SELECT s FROM Semester s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR CAST(s.year AS string) LIKE CONCAT('%', :query, '%')")
    fun findByNameOrYearContainingIgnoreCasePage(@Param("query") query: String, pageable: Pageable): Page<Semester>

    override fun findAll(pageable: Pageable): Page<Semester>

    fun findByIsActiveTrue(): List<Semester>

    @Query("SELECT s FROM Semester s WHERE s.isActive = true AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR CAST(s.year AS string) LIKE CONCAT('%', :query, '%'))")
    fun findActiveSemestersByNameOrYearContainingIgnoreCase(@Param("query") query: String): List<Semester>
}